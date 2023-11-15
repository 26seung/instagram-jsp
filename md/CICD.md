


오류 : exec /usr/local/openjdk-11/bin/java: exec format error

#### Ubuntu Server에 CodeDeploy 에이전트를 설치
```
sudo apt update
sudo apt install ruby-full
sudo apt install wget
cd /home/ubuntu
wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
```
서비스 실행중인지 확인 : sudo service codedeploy-agent status
sudo service codedeploy-agent start
sudo service codedeploy-agent status