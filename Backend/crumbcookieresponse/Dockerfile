FROM openjdk:17-jdk-slim
LABEL maintainer="Peter Cheng"
ADD ./crumbcookieresponse-0.0.1-SNAPSHOT.jar crumbcookieresponse-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","crumbcookieresponse-0.0.1-SNAPSHOT.jar"]
CMD ["sh", "-c", "sleep 10 && java -jar crumbcookieresponse-0.0.1-SNAPSHOT.jar"]
EXPOSE 8100