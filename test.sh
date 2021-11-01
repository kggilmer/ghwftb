#!/bin/bash

excluded_services=("transcribestreaming" "timestreamwrite" "timestreamquery")
shopt -s nullglob
for aws_model_file in codegen/sdk/aws-models/*.json
do
  MODEL_FILENAME=${aws_model_file##*/}    # Extract the filename from the path
  SERVICE_NAME=$(echo "$MODEL_FILENAME" | cut -d. -f1 ) # Extract the service name from the filename
  excluded=$(echo "${excluded_services[@]}" | grep -ow "$SERVICE_NAME" | wc -w)
  if [[ $excluded == 0 ]]; then
    echo "Building docs for $SERVICE_NAME"                           # delete src for generated SDK
  else
    echo "Ignoring excluded service $SERVICE_NAME"
  fi
done