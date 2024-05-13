FROM mongo:4.2.21
LABEL description="Mongo 4.2.21 + Init Script extra DB"
COPY ./mongo-init.sh /docker-entrypoint-initdb.d/

#docker build  -t devdocker.wifa.uni-leipzig.de:5000/transit/mongo:4.2.21 .
#docker push  devdocker.wifa.uni-leipzig.de:5000/transit/mongo:4.2.21