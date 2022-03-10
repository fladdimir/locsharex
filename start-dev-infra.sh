#!/bin/bash

# start all supporting services for local development

# nginx
docker-compose -f nginx/docker-compose.yml up &

# keycloak
docker-compose -f keycloak/keycloak-x/docker-compose.yml up &

# be-db
docker-compose -f be/docker-compose.yml up &

wait
