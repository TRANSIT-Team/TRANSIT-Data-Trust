#syntax=docker/dockerfile:1.3

FROM gradle:7.4.2-jdk17 as builder
WORKDIR /app
RUN echo 
COPY geoservice/ /app/
# && sleep 10d
RUN gradle build -x test

FROM eclipse-temurin:17-jre-alpine
LABEL description="Transit Geoservice"
RUN mkdir /app
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
RUN apk add curl
RUN curl -sfL $(curl -s https://api.github.com/repos/powerman/dockerize/releases/latest | grep -i /dockerize-$(uname -s)-$(uname -m)\" | cut -d\" -f4) | install /dev/stdin /usr/local/bin/dockerize
COPY start.sh start.sh



#docker build -t devdocker.wifa.uni-leipzig.de:5000/transit/spring-geoservice:latest .
#docker push  devdocker.wifa.uni-leipzig.de:5000/transit/spring-geoservice:latest

