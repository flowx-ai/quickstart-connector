FROM openjdk:11.0.12-jdk-slim-buster
ENV PORT 8080
EXPOSE $PORT

ENV CONFIG_PROFILE dev
ENV LOGGING_CONFIG_FILE logback-spring.xml
ENV CONFIG_LOCATION /opt/application-${CONFIG_PROFILE}.yaml

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

WORKDIR /opt

COPY target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -noverify -XX:+AlwaysPreTouch -Duser.timezone=UTC -Djava.security.egd=file:/dev/./urandom -Dspring.cloud.config.enabled=false -Dspring.profiles.active=${CONFIG_PROFILE} -Dlogging.config=classpath:${LOGGING_CONFIG_FILE} -jar app.jar"]
