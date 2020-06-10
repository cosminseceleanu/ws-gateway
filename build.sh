source deployment/env/local.cosmin.env

#echo "Run tests"
#mvn -s .mvn/settings.xml clean verify

echo "Build jar"
mvn -s .mvn/settings.xml -pl ws-gateway-api clean package spring-boot:repackage -DskipTests || exit 1

echo "Build docker image"
cp -f ws-gateway-api/target/ws-gateway-api-*.jar deployment/gateway/gateway.jar || exit 1
docker build -t ws-gateway:${VERSION} deployment/gateway || exit 1
rm -rf deployment/gateway/gateway.jar || exit 1

echo "Done"

