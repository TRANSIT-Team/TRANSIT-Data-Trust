#syntax=docker/dockerfile:1.3

FROM gradle:7.4.2-jdk17 as builder
WORKDIR /app
RUN echo 
COPY graph-based-policy-machine/ /app/

RUN apt update && apt -y install  curl
RUN gradle test -i --tests "com.transit.graphbased_v2.GraphBasedV2ApplicationTests.testCreateEntityNodeMultipleTimesParallel"
   
