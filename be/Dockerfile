
# build native image first:
# gradle clean assemble nativeImage

FROM ubuntu
RUN mkdir /opt/app
COPY heroku/prepare_jdbc_url.sh /opt/app/prepare_jdbc_url.sh
ENV MICRONAUT_ENVIRONMENTS testAuth,static,heroku,okta
COPY build/native-image/application /opt/app/application
CMD ["/bin/bash", "-c", ". /opt/app/prepare_jdbc_url.sh && /opt/app/application -Xmx256m"]
