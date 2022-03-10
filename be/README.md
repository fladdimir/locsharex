# be

## authentication

<https://guides.micronaut.io/micronaut-oauth2-oidc-google/guide/index.html>

## on annotation processing error during vscode debugging

gradle eclipse

> regenerates .factorypath matching build.gradle

## run

gradle assemble

java -jar build/libs/locsharex-0.1-all.jar

## profiles

export MICRONAUT_ENVIRONMENTS=testAuth,static,heroku,localDb
export JWT_SECRET=01234567890123456789012345678912

## heroku

heroku container:push web --app locsharex
heroku container:release web --app locsharex

heroku config:set OKTA_CLIENT_ID=${OKTA_CLIENT_ID} --app locsharex
heroku config:set OKTA_CLIENT_SECRET=${OKTA_CLIENT_SECRET} --app locsharex
heroku config:set OKTA_ISSUER_URL=${OKTA_ISSUER_URL} --app locsharex
heroku config:set OKTA_END_SESSION_URL=${OKTA_END_SESSION_URL} --app locsharex
heroku config:set JWT_SECRET=${JWT_SECRET} --app locsharex

## graal

sdk use java 21.0.0.r11-grl

gradle nativeImage

## log level

export LOGGER_LEVELS_ROOT=DEBUG

## debug app

delete ./bin/test/

## static resources

rm -rf ./src/main/resources/static/build
cp -r ../fe/build ./src/main/resources/static/build
