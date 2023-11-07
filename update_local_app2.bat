@echo off

gradle clean

gradle build

docker buildx build . -t fcg_webapp

docker-compose up -d
