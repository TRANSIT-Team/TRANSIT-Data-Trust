#syntax=docker/dockerfile:1.3

FROM gradle:7.4.2-jdk17 as builder
LABEL stage=builder-backend-spring-main-transit
WORKDIR /app
RUN echo 
COPY spring-app/ /app/
COPY build.sh build.sh
COPY spring-app/src/main/resources/conf/redisson-prod.yaml /redisson.yaml
# && sleep 10d
# COPY /resolv.conf /tmp/resolv.conf
#RUN nohup --mount=type=secret,id=TRANSIT-TEST-SECURITY-CONFIG-ACCESS-CONTROL gradle localh2 -b access-control-filter-properties/property-level-access-control-filter/build.gradle

#RUN --mount=type=secret,id=TRANSIT-TEST-SECURITY-CONFIG-BACKEND gradle build 
RUN apt update && apt -y install  curl
RUN curl -sfL $(curl -s https://api.github.com/repos/powerman/dockerize/releases/latest | grep -i /dockerize-$(uname -s)-$(uname -m)\" | cut -d\" -f4) | install /dev/stdin /usr/local/bin/dockerize
RUN --mount=type=secret,id=TRANSIT-TEST-SECURITY-CONFIG-BACKEND \
   #  nohup gradle localh2 -b /access-control-mock/access-control-filter-properties/property-level-access-control-filter/build.gradle &\
   #  dockerize -wait tcp://localhost:8085 -timeout 600s  && \
   #cp /etc/resolv.conf.override /etc/resolv.conf && \
   /bin/bash build.sh
   
 #  cp TRANSIT-TEST-SECURITY-CONFIG-BACKEND TRANSIT-TEST-SECURITY-CONFIG-BACKEND.properties &&\
 #   gradle build  
    #-x test

FROM eclipse-temurin:17-jre-alpine
LABEL description="Transit Backend"
RUN mkdir /app
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
RUN apk add curl
RUN curl -sfL $(curl -s https://api.github.com/repos/powerman/dockerize/releases/latest | grep -i /dockerize-$(uname -s)-$(uname -m)\" | cut -d\" -f4) | install /dev/stdin /usr/local/bin/dockerize
COPY start.sh start.sh
COPY start-test.sh start-test.sh


#docker build  -t devdocker.wifa.uni-leipzig.de:5000/transit/spring-backend:latest .
#docker push  devdocker.wifa.uni-leipzig.de:5000/transit/spring-backend:latest

