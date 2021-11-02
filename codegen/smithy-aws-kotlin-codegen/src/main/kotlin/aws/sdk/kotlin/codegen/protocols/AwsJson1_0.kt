/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
package aws.sdk.kotlin.codegen.protocols

import aws.sdk.kotlin.codegen.protocols.core.AwsHttpBindingProtocolGenerator
import aws.sdk.kotlin.codegen.protocols.json.AwsJsonHttpBindingResolver
import aws.sdk.kotlin.codegen.protocols.json.AwsJsonProtocolMiddleware
import aws.sdk.kotlin.codegen.protocols.json.JsonHttpBindingProtocolGenerator
import software.amazon.smithy.aws.traits.protocols.AwsJson1_0Trait
import software.amazon.smithy.kotlin.codegen.rendering.protocol.HttpBindingResolver
import software.amazon.smithy.kotlin.codegen.rendering.protocol.ProtocolGenerator
import software.amazon.smithy.kotlin.codegen.rendering.protocol.ProtocolMiddleware
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.*

/**
 * Handles generating the aws.protocols#awsJson1_0 protocol for services.
 *
 * @inheritDoc
 * @see AwsHttpBindingProtocolGenerator
 */
class AwsJson1_0 : JsonHttpBindingProtocolGenerator() {

    override val protocol: ShapeId = AwsJson1_0Trait.ID

    override fun getDefaultHttpMiddleware(ctx: ProtocolGenerator.GenerationContext): List<ProtocolMiddleware> {
        val httpMiddleware = super.getDefaultHttpMiddleware(ctx)
        val awsJsonMiddleware = listOf(
            AwsJsonProtocolMiddleware(ctx.settings.service, "1.0"),
        )

        return httpMiddleware + awsJsonMiddleware
    }

    override fun getProtocolHttpBindingResolver(model: Model, serviceShape: ServiceShape): HttpBindingResolver =
        AwsJsonHttpBindingResolver(model, serviceShape, "application/x-amz-json-1.0")
}
