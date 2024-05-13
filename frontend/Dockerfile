FROM node:18.13.0-bullseye as build
LABEL stage=builder-frontend-angular-transit
WORKDIR /transit-app

RUN npm install -g @angular/cli


#COPY ./transit-app/package.json .
COPY ./transit-app/ /transit-app/
RUN npm install
#RUN npm install -g npm@8.15.0
RUN ng build --configuration production
#ENTRYPOINT /bin/sh -c 'sleep 2d'
#ENTRYPOINT /bin/sh -c 'nginx -s reload'

FROM nginx:1.23.0 as runtime
#RUN rm -rf /usr/share/nginx/html/* && rm -rf /etc/nginx/nginx.conf
#COPY ./config/nginx.conf /etc/nginx/nginx.conf
COPY ./config/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /transit-app/dist/transit-app /usr/share/nginx/html

#docker build -t transit-angular-local:latest .
