cd /home/ubuntu/deploy
# AWS CLI
echo pullDocker start...
pwd
ls
echo ECR_REPO1 : $ECR_REPO
echo ECR_REPO2 : "$ECR_REPO"
echo ECR_REPO3 : ${ECR_REPO}
#aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 076796029005.dkr.ecr.ap-northeast-2.amazonaws.com
#aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com
$ECR_REPO
# pull docker image
#if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
#then
#  docker-compose -f /deploy/docker-compose.dev.yml pull
#elif [ "$DEPLOYMENT_GROUP_NAME" == "stage" ]
#then
#  docker-compose -f /deploy/docker-compose.stage.yml pull
#elif [ "$DEPLOYMENT_GROUP_NAME" == "production" ]
#then
  docker-compose -f docker-compose.yml pull
#fi