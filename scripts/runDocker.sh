cd /home/ubuntu/deploy

#if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
#then
#  # Remove any anonymous volumes attached to containers
#  docker-compose -f /deploy/docker-compose.dev.yml rm -v
#  # build images and run containers
#  docker-compose -f /deploy/docker-compose.dev.yml up --detach --renew-anon-volumes
#elif [ "$DEPLOYMENT_GROUP_NAME" == "stage" ]
#then
#  # Remove any anonymous volumes attached to containers
#  docker-compose -f /deploy/docker-compose.stage.yml rm -v
#  # build images and run containers
#  docker-compose -f /deploy/docker-compose.stage.yml up --detach --renew-anon-volumes
#elif [ "$DEPLOYMENT_GROUP_NAME" == "production" ]
#then
  # Remove any anonymous volumes attached to containers
  docker-compose -f /deploy/docker-compose.yml rm
  # build images and run containers
  docker-compose -f /deploy/docker-compose.yml up -d
#fi