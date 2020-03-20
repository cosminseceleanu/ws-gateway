FROM openjdk:8-jdk

EXPOSE 9000
RUN mkdir -p /opt/scala-play/bin
WORKDIR /opt/scala-play/bin

ARG APP_VERSION

COPY app.zip /opt/scala-play/app.zip
RUN rm -rf /opt/scala-play/bin/* && \
    unzip -o /opt/scala-play/app.zip && \
    mv application-$APP_VERSION app

ENV JAVA_OPTS=""
ENV PLAY_OPTS=""

CMD app/bin/application -Dplay.http.secret.key=$APP_SECRET $PLAY_OPTS $JAVA_OPTS
#CMD exec /bin/sh -c "trap : TERM INT; (while true; do sleep 1000; done) & wait"
