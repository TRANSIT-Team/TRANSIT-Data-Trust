#!/bin/sh
dockerize -wait tcp://kong-db:5432 -timeout 600s
nohup ./docker-entrypoint.sh kong docker-start > /dev/null 2>&1 < /dev/null &
dockerize -wait file:///usr/local/kong/.kong_env -timeout 600s
kong migrations bootstrap -c /usr/local/kong/.kong_env
kong stop
rm -r  /usr/local/kong/*
rm  /usr/local/kong/.kong_env