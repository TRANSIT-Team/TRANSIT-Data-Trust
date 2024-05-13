#!/bin/sh
sleep 15s &&
dockerize -wait file:///run/secrets/TRANSIT_ACCESS_CONTROL_CORE_DB_USER_NAME_NEO -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_ACCESS_CONTROL_CORE_DB_USER_PASSWORD_NEO -timeout 600s &&


cd /run/secrets/ &&
mkdir -p spring/neo4j/authentication/ &&

cp TRANSIT_ACCESS_CONTROL_CORE_DB_USER_NAME_NEO spring/neo4j/authentication/&&
cp TRANSIT_ACCESS_CONTROL_CORE_DB_USER_PASSWORD_NEO spring/neo4j/authentication/ &&


cd spring/neo4j/authentication/ &&
mv TRANSIT_ACCESS_CONTROL_CORE_DB_USER_NAME_NEO  username &&
mv TRANSIT_ACCESS_CONTROL_CORE_DB_USER_PASSWORD_NEO  password &&

java -jar /app/app.jar