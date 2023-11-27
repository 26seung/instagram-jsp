#!/bin/bash

# start
echo deployDocker script started on `date`

# go to "docker-compose" file directory
cd /home/ubuntu/deploy
pwd

# AWS Systems Manager (use Parameter Store)
export AWS_DEFAULT_REGION=$(aws ssm get-parameter --name /account/config/region --query 'Parameter.Value' --output text --with-decryption)
export AWS_ACCOUNT_ID=$(aws ssm get-parameter --name /account/config/id --query 'Parameter.Value' --output text --with-decryption)
export IMAGE_REPO_NAME=$(aws ssm get-parameter --name /ecr/repo/photo --query 'Parameter.Value' --output text --with-decryption)
export IMAGE_TAG="latest"


# ECR login
aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com

# pull docker image
echo docker images pull...
docker-compose -f docker-compose.yml pull

# remove docker containers
echo docker container down...
docker-compose -f docker-compose.yml down

# run docker containers
echo docker container start...
docker-compose -f docker-compose.yml up -d

# remove any anonymous images
docker image prune -af

# finish
echo deployDocker script end on `date`