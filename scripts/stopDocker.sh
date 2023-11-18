#!/bin/bash

#docker stop $(docker ps -a -q)

echo ECR_REPO1 : $ECR_REPO
echo ECR_REPO2 : "$ECR_REPO"
echo "ECR_REPO3 : $ECR_REPO"
echo ECR_REPO4 : ${ECR_REPO}