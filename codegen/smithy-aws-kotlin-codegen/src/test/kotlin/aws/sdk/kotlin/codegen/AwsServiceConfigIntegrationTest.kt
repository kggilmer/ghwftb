/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package aws.sdk.kotlin.codegen

import org.junit.jupiter.api.Test
import software.amazon.smithy.kotlin.codegen.core.KotlinWriter
import software.amazon.smithy.kotlin.codegen.model.expectShape
import software.amazon.smithy.kotlin.codegen.rendering.ClientConfigGenerator
import software.amazon.smithy.kotlin.codegen.test.*
import software.amazon.smithy.model.shapes.ServiceShape

class AwsServiceConfigIntegrationTest {
    @Test
    fun testServiceConfigurationProperties() {
        val model = """
            namespace com.test

            use aws.protocols#awsJson1_1
            use aws.api#service

            @service(sdkId: "service with overrides", endpointPrefix: "service-with-overrides")
            @awsJson1_1
            service Example {
                version: "1.0.0",
                operations: [GetFoo]
            }

            operation GetFoo {}
        """.toSmithyModel()

        val serviceShape = model.expectShape<ServiceShape>("com.test#Example")

        val testCtx = model.newTestContext(serviceName = "Example")
        val writer = KotlinWriter("com.test")

        val renderingCtx = testCtx.toRenderingContext(writer, serviceShape)
            .copy(integrations = listOf(AwsServiceConfigIntegration()))

        ClientConfigGenerator(renderingCtx, detectDefaultProps = false).render()
        val contents = writer.toString()

        val expectedProps = """
    override val credentialsProvider: CredentialsProvider = builder.credentialsProvider ?: DefaultChainCredentialsProvider()
    val endpointResolver: AwsEndpointResolver = builder.endpointResolver ?: DefaultEndpointResolver()
    override val region: String = requireNotNull(builder.region) { "region is a required configuration property" }
"""
        contents.shouldContainOnlyOnceWithDiff(expectedProps)

        val expectedImpl = """
        override var credentialsProvider: CredentialsProvider? = null
        override var endpointResolver: AwsEndpointResolver? = null
        override var region: String? = null
"""
        contents.shouldContainOnlyOnceWithDiff(expectedImpl)
    }
}
