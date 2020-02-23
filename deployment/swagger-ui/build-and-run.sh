#!/usr/bin/env bash

API_SPECIFICATION=../../docs/api/open-api-spec.yml

cp -f $API_SPECIFICATION api-spec.yml

docker stop swagger-ui
docker rm swagger-ui

docker build --build-arg API_SPEC_PATH=api-spec.yml -t ws-gateway/swagger-ui .
docker run --name swagger-ui -p 8080:8080 -e SWAGGER_JSON=/swagger/api-spec.yml -d ws-gateway/swagger-ui

rm -rf api-spec.yml