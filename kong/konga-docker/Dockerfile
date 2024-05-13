FROM  pantsel/konga:0.14.9
LABEL description="Konga 0.14.9 + Docker Secrets Lists"
COPY ./connections.js /app/config/connections.js
COPY ./Token.js /app/api/services/Token.js


RUN apk update && apk add curl

#docker build  -t devdocker.wifa.uni-leipzig.de:5000/transit/konga:0.14.9 .
#docker push  devdocker.wifa.uni-leipzig.de:5000/transit/konga:0.14.9