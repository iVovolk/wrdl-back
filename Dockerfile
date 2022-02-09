FROM openjdk:17.0.2-jdk-slim

ENV APP_USER ktor
ENV APP_UID 1024
ENV APP_GROUP ktor
ENV APP_GID 1024

RUN addgroup --gid $APP_GID $APP_GROUP &&\
    adduser --disabled-password --gecos '' --gid $APP_GID --uid $APP_UID $APP_USER &&\
    mkdir -p /app/dbdata &&\
    chown -R $APP_USER:$APP_GID /app

USER $APP_USER

COPY ./build/libs/wrdl.jar /app/wrdl.jar
WORKDIR /app

CMD ["java", "-server", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=50.0", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "wrdl.jar"]
