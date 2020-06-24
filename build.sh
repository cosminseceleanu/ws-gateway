source deployment/env/local.cosmin.env

set -e

function buildGatewayImage {
  echo "Run tests"
  mvn -s .mvn/settings.xml clean verify

  echo "Build jar"
  mvn -s .mvn/settings.xml -pl ws-gateway-api clean package spring-boot:repackage -DskipTests

  echo "Build docker image"
  cp -f ws-gateway-api/target/ws-gateway-api-*.jar deployment/gateway/gateway.jar
  docker build -t ws-gateway:${VERSION} deployment/gateway
  rm -rf deployment/gateway/gateway.jar

  echo "Done"
}


if [ $1 = "gateway" ]; then
  echo "Build gateway"
  buildGatewayImage
elif [ $1 = "prometheus" ]; then
  echo "Build prometheus"
  docker build -t ws-gateway/prometheus:${VERSION} deployment/prometheus
  echo "Done"
else
  echo "Build gateway"
  buildGatewayImage
fi


