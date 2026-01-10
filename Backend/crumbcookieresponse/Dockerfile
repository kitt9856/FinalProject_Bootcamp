FROM openjdk:17.0.2-jdk-slim-bullseye
LABEL maintainer="Peter Cheng"
ADD target/crumbcookieresponse-0.0.2-SNAPSHOT.jar crumbcookieresponse-0.0.2-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","crumbcookieresponse-0.0.2-SNAPSHOT.jar"]
CMD ["sh", "-c", "sleep 10 && java -jar crumbcookieresponse-0.0.2-SNAPSHOT.jar"]
EXPOSE 8100