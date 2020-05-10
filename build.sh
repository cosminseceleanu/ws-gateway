API_SPECIFICATION=docs/api/open-api-spec.yml

source deployment/env/local.cosmin.env

echo "Build swagger-ui image"
cp -f $API_SPECIFICATION deployment/swagger-ui/api-spec.yml

docker build --build-arg API_SPEC_PATH=api-spec.yml -t ws-gateway/swagger-ui:${VERSION} deployment/swagger-ui

rm -rf deployment/swagger-ui/api-spec.yml

echo "Done building swagger-ui image"

#echo "Build gateway project"
#sbt clean
#sbt dist
#
#echo "Build gateway image"
#cp -f application/target/universal/*.zip deployment/gateway/app.zip
#docker build --build-arg APP_VERSION=1.0-SNAPSHOT -t ws-gateway:${VERSION} deployment/gateway
#rm -rf deployment/gateway/app.zip

echo "Done"

