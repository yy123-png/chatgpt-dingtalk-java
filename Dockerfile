FROM openjdk:8-jdk-alpine

RUN mkdir "/app"

ADD target/chatgpt-java.jar /app/chatgpt-java.jar

COPY config.json /app/config.json

ENV JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF8 -Duser.timezone=GMT+08"

ENTRYPOINT exec java $JAVA_OPTS -jar /app/chatgpt-java.jar