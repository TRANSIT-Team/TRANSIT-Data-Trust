FROM httpd:2.4.54 as extract

RUN apt-get update && apt-get install -y git unzip
ARG King_FOR_KONG_DIST=https://github.com/ligreman/king/releases/download/v1.2.1/King-for-Kong-v1.2.1.zip
ADD $King_FOR_KONG_DIST /tmp/king/



RUN (cd /tmp/king && \
    unzip /tmp/king/King-for-Kong-*.zip && \
    rm /tmp/king/King-for-Kong-*.zip) || true

FROM httpd:2.4.54

COPY --from=extract /tmp/king /usr/local/apache2/htdocs/




#docker build  -t devdocker.wifa.uni-leipzig.de:5000/transit/king-for-kong:1.2.1 .
#docker push  devdocker.wifa.uni-leipzig.de:5000/transit/king-for-kong:1.2.1

