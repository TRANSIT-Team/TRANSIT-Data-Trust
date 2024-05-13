#!/bin/sh
sleep 15s &&
dockerize -wait tcp://backend-db:5432 -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_DB_USER_NAME -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_DB_USER_PASSWORD -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_AUTH_CLIENT_ID -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_AUTH_CLIENT_SECRET -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_AUTH_SCOPE -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_AUTH_ISSUER_URI -timeout 600s &&

dockerize -wait file:///run/secrets/TRANSIT_BACKEND_ADMIN_CLIENT_URL -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_ADMIN_CLIENT_REALM -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_ADMIN_CLIENT_ID -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_BACKEND_ADMIN_CLIENT_SECRET -timeout 600s &&

dockerize -wait file:///run/secrets/TRANSIT_KEYCLOAK_SERVER_CLIENT_ID -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_KEYCLOAK_SERVER_CLIENT_SECRET -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_KEYCLOAK_SERVER_AUTHORIZATION_GRANT_TYPE -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_KEYCLOAK_SERVER_ISSUER_URI -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_KEYCLOAK_SERVER_TOKEN_URI -timeout 600s &&
dockerize -wait file:///run/secrets/RIGHTS_SERVICE_BASE_PATH_CORE -timeout 600s &&

dockerize -wait file:///run/secrets/TRANSIT_MAIL_HOST -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_MAIL_PORT -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_MAIL_USERNAME -timeout 600s &&
dockerize -wait file:///run/secrets/TRANSIT_MAIL_PASSSWORD -timeout 600s &&

cd /run/secrets/ &&
mkdir -p spring/datasource/ &&
mkdir -p spring/security/oauth2/client/provider/keycloak/ &&
mkdir -p spring/security/oauth2/client/registration/keycloak/ &&

mkdir -p spring/security/oauth2/client/provider/keycloak-server/ &&
mkdir -p spring/security/oauth2/client/registration/keycloak-server/ &&

mkdir -p keycloak/admin/client/ &&

mkdir -p rights/service/ &&

mkdir -p spring/mail/ &&

cp TRANSIT_BACKEND_DB_USER_NAME spring/datasource/ &&
cp TRANSIT_BACKEND_DB_USER_PASSWORD spring/datasource/ &&
cp TRANSIT_BACKEND_AUTH_CLIENT_ID spring/security/oauth2/client/registration/keycloak/ &&
cp TRANSIT_BACKEND_AUTH_CLIENT_SECRET spring/security/oauth2/client/registration/keycloak/ &&
cp TRANSIT_BACKEND_AUTH_SCOPE spring/security/oauth2/client/registration/keycloak/ &&
cp TRANSIT_BACKEND_AUTH_ISSUER_URI spring/security/oauth2/client/provider/keycloak/ &&

cp TRANSIT_BACKEND_ADMIN_CLIENT_URL keycloak/admin/client/ &&
cp TRANSIT_BACKEND_ADMIN_CLIENT_REALM keycloak/admin/client/ &&
cp TRANSIT_BACKEND_ADMIN_CLIENT_ID keycloak/admin/client/ &&
cp TRANSIT_BACKEND_ADMIN_CLIENT_SECRET keycloak/admin/client/ &&

cp TRANSIT_KEYCLOAK_SERVER_CLIENT_ID spring/security/oauth2/client/registration/keycloak-server/ &&
cp TRANSIT_KEYCLOAK_SERVER_CLIENT_SECRET spring/security/oauth2/client/registration/keycloak-server/ &&
cp TRANSIT_KEYCLOAK_SERVER_AUTHORIZATION_GRANT_TYPE spring/security/oauth2/client/registration/keycloak-server/ &&
cp TRANSIT_KEYCLOAK_SERVER_ISSUER_URI spring/security/oauth2/client/provider/keycloak-server/ &&
cp TRANSIT_KEYCLOAK_SERVER_TOKEN_URI spring/security/oauth2/client/provider/keycloak-server/ &&

cp RIGHTS_SERVICE_BASE_PATH_CORE rights/service/ &&

cp TRANSIT_MAIL_HOST spring/mail/ &&
cp TRANSIT_MAIL_PORT spring/mail/ &&
cp TRANSIT_MAIL_USERNAME spring/mail/ &&
cp TRANSIT_MAIL_PASSSWORD spring/mail/ &&

cd spring/datasource/ &&
mv TRANSIT_BACKEND_DB_USER_NAME  username &&
mv TRANSIT_BACKEND_DB_USER_PASSWORD  password &&
cd /run/secrets/spring/security/oauth2/client/registration/keycloak/ &&
mv TRANSIT_BACKEND_AUTH_CLIENT_ID client-id &&
mv TRANSIT_BACKEND_AUTH_CLIENT_SECRET client-secret &&
mv TRANSIT_BACKEND_AUTH_SCOPE scope &&
cd /run/secrets/spring/security/oauth2/client/provider/keycloak/ &&
mv TRANSIT_BACKEND_AUTH_ISSUER_URI issuer-uri &&


cd /run/secrets/keycloak/admin/client/ &&
mv TRANSIT_BACKEND_ADMIN_CLIENT_URL url &&
mv TRANSIT_BACKEND_ADMIN_CLIENT_REALM realm &&
mv TRANSIT_BACKEND_ADMIN_CLIENT_ID id &&
mv TRANSIT_BACKEND_ADMIN_CLIENT_SECRET secret &&

cd /run/secrets/spring/security/oauth2/client/registration/keycloak-server/ &&
mv TRANSIT_KEYCLOAK_SERVER_CLIENT_ID client-id &&
mv TRANSIT_KEYCLOAK_SERVER_CLIENT_SECRET client-secret &&
mv TRANSIT_KEYCLOAK_SERVER_AUTHORIZATION_GRANT_TYPE authorization-grant-type &&
cd /run/secrets/spring/security/oauth2/client/provider/keycloak-server/ &&
mv TRANSIT_KEYCLOAK_SERVER_ISSUER_URI issuer-uri &&
mv TRANSIT_KEYCLOAK_SERVER_TOKEN_URI token-uri &&
cd /run/secrets/rights/service/ &&
mv RIGHTS_SERVICE_BASE_PATH_CORE base-path &&

cd /run/secrets/spring/mail/ &&
mv TRANSIT_MAIL_HOST host &&
mv TRANSIT_MAIL_PORT port &&
mv TRANSIT_MAIL_USERNAME username &&
mv TRANSIT_MAIL_PASSSWORD password &&


java -Xms12G -Xmx48G -Xss1G -jar /app/app.jar