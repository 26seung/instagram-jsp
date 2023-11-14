# AWS CLI
aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com

# pull docker image
if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
then
  docker-compose -f /deploy/docker-compose.dev.yml pull
elif [ "$DEPLOYMENT_GROUP_NAME" == "stage" ]
then
  docker-compose -f /deploy/docker-compose.stage.yml pull
elif [ "$DEPLOYMENT_GROUP_NAME" == "production" ]
then
  docker-compose -f /deploy/docker-compose.yml pull
fi