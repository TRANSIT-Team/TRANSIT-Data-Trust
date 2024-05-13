    FROM kong:2.8.1

    LABEL description="Kong 2.8.1 + kong-oidc, openssl  plugin + dockerize"

    USER root
    RUN apk update && apk add git unzip luarocks curl gcc musl-dev
    RUN luarocks install kong-oidc

    RUN luarocks install luaossl OPENSSL_DIR=/usr/local/kong CRYPTO_DIR=/usr/local/kong

    RUN curl -sfL $(curl -s https://api.github.com/repos/powerman/dockerize/releases/latest | grep -i /dockerize-$(uname -s)-$(uname -m)\" | cut -d\" -f4) | install /dev/stdin /usr/local/bin/dockerize

    COPY start-bootstrap.sh start-bootstrap.sh
    COPY start-update.sh start-update.sh
    COPY start.sh start.sh
    COPY nginx.conf  /usr/local/kong/nginx.conf
    COPY nginx-kong.conf  /usr/local/kong/nginx-kong.conf
    COPY nginx2.conf /usr/local/openresty/nginx/conf/nginx.conf
    COPY kong_defaults.lua  /usr/local/share/lua/5.1/kong/templates/kong_defaults.lua
    
    USER kong


#docker build  -t devdocker.wifa.uni-leipzig.de:5000/transit/kong:2.8.1 .
#docker push  devdocker.wifa.uni-leipzig.de:5000/transit/kong:2.8.1