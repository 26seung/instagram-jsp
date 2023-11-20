#!/bin/bash

echo runDocker started on `date`

# docker-compose 파일 위치로 이동
cd /home/ubuntu/deploy
pwd

# Remove any anonymous volumes attached to containers
echo runDocker start...
docker-compose -f docker-compose.yml down
# build images and run containers
docker-compose -f docker-compose.yml up -d
# 사용하지 않는 모든 이미지 삭제
docker image prune -af