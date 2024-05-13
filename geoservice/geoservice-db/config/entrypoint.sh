#!/bin/sh

bash /usr/local/bin/docker-entrypoint.sh postgres &&
nohup postgres &
bash /wait-for-it.sh localhost:5432 -s -t 600 -- ogr2ogr -f "PostgreSQL" PG:"dbname=$POSTGRES_DB user=$POSTGRES_USER password=$POSTGRES_PASSWORD" /jsondata/zipCodesGermanyPolygons.geojson -nln zipCodesGermanyPolygons &&
psql -U $POSTGRES_USER -d $POSTGRES_DB -a -f "/polyfunctions.sql"

sleep infinity

