FROM openjdk:11
COPY ./build/libs/gateway-0.0.1-SNAPSHOT.jar /usr/src/gateway/
WORKDIR /usr/src/gateway
CMD ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]