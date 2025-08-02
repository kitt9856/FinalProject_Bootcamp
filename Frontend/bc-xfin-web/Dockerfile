FROM openjdk:17-jdk-slim
LABEL maintainer="Peter Cheng"
ADD target/bc-xfin-web-0.0.1-SNAPSHOT.jar bc-xfin-web-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","bc-xfin-web-0.0.1-SNAPSHOT.jar"]
EXPOSE 8200