source deployment/env/local.cosmin.env
export VERSION=${VERSION}

docker-compose -f deployment/docker-compose.yml down
