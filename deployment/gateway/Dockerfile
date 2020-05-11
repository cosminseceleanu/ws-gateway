FROM openjdk:11-jdk

EXPOSE 8080
EXPOSE 8081
#ignite
EXPOSE 47100
#ignite discovery
EXPOSE 47500

RUN mkdir -p /opt/gateway
RUN mkdir -p /opt/gateway/target
WORKDIR /opt/gateway/bin

COPY gateway.jar ./

ENV JAVA_OPTS=""
ENV APP_OPTS=""

CMD java $JAVA_OPTS $APP_OPTS -jar gateway.jar
#CMD exec /bin/sh -c "trap : TERM INT; (while true; do sleep 1000; done) & wait"
