#!/bin/sh
dockerize -wait tcp://kong-db:5432 -timeout 600s
./docker-entrypoint.sh kong docker-start