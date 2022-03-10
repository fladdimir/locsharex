# gradle native image build (quarkus image)
FROM quay.io/quarkus/ubi-quarkus-native-image:21.3.1-java11 AS be_build
COPY --chown=quarkus:quarkus be/gradlew /code/gradlew
COPY --chown=quarkus:quarkus be/gradle /code/gradle
COPY --chown=quarkus:quarkus be/build.gradle /code/
COPY --chown=quarkus:quarkus be/settings.gradle /code/
COPY --chown=quarkus:quarkus be/gradle.properties /code/
USER quarkus
WORKDIR /code
COPY be/src /code/src
RUN ./gradlew assemble --info
RUN ./gradlew nativeImage --info

# build fe app
FROM node:lts-alpine as fe_build
WORKDIR /app
COPY fe/package*.json ./
RUN yarn install
COPY fe .
RUN yarn build

# heroku image
FROM nginx
# app dir
RUN mkdir /opt/app
WORKDIR /opt/app
# helper scripts
COPY be/heroku ./heroku
RUN chmod a+x ./heroku/prepare_jdbc_url.sh
# nginx config
COPY heroku/etc_nginx/ /etc/nginx/
# fe build
COPY --from=fe_build app/build/ /usr/share/nginx/html/
# be build
COPY --from=be_build /code/build/native-image/application application

ENV MICRONAUT_ENVIRONMENTS testAuth,heroku,okta
CMD ["/bin/bash", "-c", "\
    envsubst < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf && nginx -g 'daemon off;' \
    & . /opt/app/heroku/prepare_jdbc_url.sh && /opt/app/application -Xmx256m \
    "]
