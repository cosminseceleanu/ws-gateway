#eval $(minikube docker-env)
#export JAVA_HOME=`/usr/libexec/java_home -v 11`

source deployment/env/local.cosmin.env
export VERSION=${VERSION}

docker-compose -f deployment/docker-compose.yml up -d
