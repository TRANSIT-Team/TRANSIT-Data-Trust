FROM postgis/postgis:15-3.3 AS base
#WORKDIR /
#ARG POSTGRES_USER
#ARG POSTGRES_PASSWORD
#ARG POSTGRES_DB

#ENV POSTGRES_USER: $POSTGRES_USER
#ENV POSTGRES_PASSWORD: $POSTGRES_PASSWORD
#ENV POSTGRES_DB: $POSTGRES_DB

ENV POSTGRES_MULTIPLE_EXTENSIONS: postgis,hstore,postgis_topology,postgis_raster,pgrouting

COPY config/dbdata/jsondata/ /jsondata
COPY config/dbdata/dbstartupscripts/startup.sql /docker-entrypoint-initdb.d/
COPY config/dbdata/dbstartupscripts/polyfunctions.sql /
COPY config/start.sh /
COPY config/entrypoint.sh /
COPY config/wait-for-it.sh /

# Installing all packages
RUN bash start.sh

ENTRYPOINT ["/entrypoint.sh"]

#docker build -t devdocker.wifa.uni-leipzig.de:5000/transit/geoservice-db:latest .
#docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD} ${REPO}
#docker push devdocker.wifa.uni-leipzig.de:5000/transit/geoservice-db:latest