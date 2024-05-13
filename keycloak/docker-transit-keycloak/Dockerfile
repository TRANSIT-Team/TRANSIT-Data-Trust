FROM quay.io/keycloak/keycloak:18.0.0 as builder

ENV KC_METRICS_ENABLED=true
ENV KC_FEATURES=token-exchange
RUN /opt/keycloak/bin/kc.sh build --db=mysql

FROM quay.io/keycloak/keycloak:18.0.0
COPY --from=builder /opt/keycloak/lib/quarkus/ /opt/keycloak/lib/quarkus/
WORKDIR /opt/keycloak
COPY docker-transit-keycloak/theme /opt/keycloak/themes/material

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start"]


#docker build  -t devdocker.wifa.uni-leipzig.de:5000/transit/keycloak:18.0.0 .
#docker push  devdocker.wifa.uni-leipzig.de:5000/transit/keycloak:18.0.0
