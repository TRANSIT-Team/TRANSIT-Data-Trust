#!/bin/sh

FILE=/app/src/main/resources/application-test.properties
if [ ! -f "$FILE" ]; then
    touch /app/src/main/resources/application-test.properties
fi
gradle build -x test