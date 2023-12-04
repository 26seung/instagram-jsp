## AWS CodeDeploy

---

>CodeDeploy는 Amazon EC2 인스턴스, 온프레미스 인스턴스, 서버리스 Lamda 함수 또는 Amazon ECS 서비스로 애플리케이션 배포를 자동화하는 배포서비스다.
CodeDeploy를 활용하면 Amazon S3 버킷, GitHub 레파지토리 또는 Bitbucket 레파지토리에 저장된 code, 서버리스 AWS Lamda 함수, 웹 및 구성파일, 실행파일, packages, 스크립트, 멀티미디어 파일 등을 거의 무제한으로 배포가 가능하다. CodeDeploy는 Amazon EC2나 AWS Lamda에 코드를 배포하는 경우 추가 비용이 부과되지 않는다`(온프레미스에 배포할 경우만 배포당 0.02USD 요금 부과).

---

### CODE DEPLOY 진행
<br />
CodeDeploy가 CI/CD 파이프라인에서 담당할 과정은 다음과 같다

- CodeBuild 빌드 작업이 완료되면 EC2에 접속하여 `CodeDeploy agent` 와 통신
- `AppSpec.yml` 파일에 작성된 명령을 순차적 실행
- `ECR`에 저장된 도커 이미지를 풀
- 풀 받은 이미지를 실행
- 실행된 프로그램이 게이트웨이에 인식이 되었는지 확인 후 이전 버전의 프로그램 종료 및 삭제

---

##### 애플리케이션 생성
<img width="789" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/81fd316a-747c-48dd-b950-d07c9deb3160">

- 애플리케이션의 이름을 입력하고 배포 대상의 서비스 유형을 선택한다.
    - EC2/온프레미스
    - AWS Lambda
    - Amazone ECS
- 여기서는 EC2 에 배포하기에 EC2 선택

<br/> 

##### 배포 그룹 생성
<img width="796" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/857d8c08-5698-471f-9d49-70b94210fac3">

- 서비스 역할 - CodeDeploy는 EC2에 접속하여 배포를 진행해야하므로 그에 맞는 역할 부여가 필요하다.
- AWSCodeDeployRole 역할에  EC2 이외에 ElasticLoadbalancing, AutoSacling 등의 권한도 같이 들어있어 해당 역할만 추가해주면 된다
- 배포 유형을 선택
    - EC2 인스턴스 하나에서 배포를 하기 때문에 `현재 위치` 를 선택.
    - 오토 스케일링 그룹이 있는 경우에는 `블루/그린` 업데이트를 선택.

<br/> 

##### 배포 그룹 생성(환경구성)
<img width="787" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/b2ee9b28-bd11-4f58-b629-74bf395fa0aa">

- 환경 구성 - EC2 인스턴스를 선택하고 배포하려는 인스턴스를 지정할 수 있는 키와 값을 선택
    - 선택된 EC2 에 설치된 `CodeDeploy agent`가 실행.

<br/> 

##### 배포 그룹 생성(기타 설정)
<img width="798" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/e33bdf49-9008-4c2c-b987-05287e875101">

- AWS CodeDeploy 에이전트 설치 - 배포할 EC2에 에이전트가 설치할 여부를 선택
    - 안 함 - 에어전트 설치가 미리 되어있을 경우 새로 설치하지 않고 업데이트도 하지 않음
    - 한 번만 - 에이언트가 설치되어있지 않을 경우 새로 설치하고, 이미 설치되어있다면 아무런 작업을 하지 않음
    - 지금 업데이트 - 에이전트가 이미 설치되어 있으면 최신버전으로 즉시 업데이트
    - 업데이트 일정 예약 -  설정한 일정에 따라 에이전트를 업데이트 하도록 설정
- 배포 구성 - 여러대의 인스턴스에 배포할 경우 인스턴스에 얼마나 많은 트래픽을 라우팅할지 결정하는 정책
    - `CodeDeployDefault.OneAtTime` - 한 번에 하나의 인스턴스에만 배포 수행. 배포 중에 서비스 중단이 없도록할 수 있지만 배포 시간이 길어질 수 있다.
    - `CodeDeployDefault.HalfAtTime` - 배포 단계에서 사용 가능한 인스턴스의 절반에 배포 수행.
    - `CodeDeployDefault.AllAtOnce` - 모든 인스턴스에 동시에 배포 수행. 가장 빠르지만 배포 중 모든 서비스가 중단될 수 있다.
- `로드 밸런싱 활성화` 체크 해제

<br/> 

##### 배포만들기
<img width="775" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/69c8089f-8cb0-4da3-90ef-3f5e9190477c">

- `PIPELINE` 사용시 설정 하므로 만들 필요 없음
- `CODEDEPLOY` 단일로 실행시에는 생성하여 테스트 가능

---

### 이 후, appspec.yml 파일 생성 필요

<img width="725" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/6e617c8a-61f8-4fc4-8b41-12dfedc9ad98">

AWS CodeDeploy는 `appspec.yml`을 통해서 어떤 파일들을, 어느 위치에 배포하고, 이후 어떤 스크립트를 실행시킬 것인지를 관리한다.  
따라서 CodeDeploy를 수행하기 위해선 `appspec.yml`파일을 `Root 디렉토리`에 생성한다.  

- 배포하려는 스프링부트 소스코드(로컬)에 appspec.yml 및 배포 후 스크립트(deploy.sh) 생성
- `buildspec.yml`파일의 `artifacts.files` 부분에 `appspec.yml 및 deploy.sh`를 추가
- 해당 소스 `GitHub` 업로드 후 `CodeBuild`하게 되면 `appspec.yml, deploy.sh, docker-compose.yml, nginx.conf`가 .zip으로 패키징되어 `S3 버킷`에 업로드 됨
- CodeDeploy 시 `S3`에 있는 .zip 파일을 unzip을 한 후 `appspec.yml` 파일을 참고하며 서비스 배포 실행

<br />

##### appspec.yml
```yaml
version: 0.0
os: linux

files:
  - source: /
    destination: /home/ubuntu/deploy
file_exists_behavior: OVERWRITE

hooks:
  AfterInstall:
    - location: scripts/deploy.sh
      timeout: 120
      runas: root
```
<br />

##### `배포스크립트`
```
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
```
- 사용된 환경변수는 `AWS Parameter Store` 사용하여 입력 구성
- `CodeBuild`에서 업로드 하였던 `ECR` 에 로그인하여, 도커 이미지를 `PULL` 한다.
- `artifacts` 가져왔던 `docker-compose.yml` 파일의 경로로 이동하여 도커컴포즈 실행
- 용량 관리를 위하여 사용하지 않는 모든 이미지는 제거

---

### 이 후, EC2 내부 설치 필요

##### Ubuntu Server에 CodeDeploy 에이전트 설치

```
#!/bin/bash
sudo apt update -y
sudo apt install ruby-full -y
sudo apt install wget
cd /home/ubuntu
wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
```
- 서비스 실행중인지 확인 명령어 : 
  - sudo service codedeploy-agent status
  - sudo service codedeploy-agent start
  - sudo service codedeploy-agent status




---

#### Parameter Store

>`aws ssm get-parameter --name /account/config/region --with-decryption` 사용시 결과값
<img width="904" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/33eaf9ce-ac9e-4125-a87e-ca2affeff6b2">

- `aws ssm get-parameter` 명령어를 통해 파라미터를 조회 할 수 있다.
- `--name` 태그를 이용하여 특정 값을 가져올 수 있다.
- 파라미터 유형이 문자열 값은 상관 없지만 보안 문자열을 사용시에는 `--with-decryption`를 통해 복호화된 값을 볼 수 있다.
- `aws ssm get-parameter --name /**/** --query 'Parameter.Value' --output text --with-decryption`사용하면 값만 추출가능
- sudo snap install aws-cli --classic


