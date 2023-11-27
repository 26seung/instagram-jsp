## AWS PIPELINE

![img.png](img.png)
코드 수정에 따른 배포 과정을 자동화 하기 위해서 `AWS PIPELINE`을 사용하여 배포를 자동화 구성하였다.  

진행 순서 : `GIT (main push)` - `Amazone SNS (Y/N)` - `CODE BUILD` - `[Elastic Container Registry]` `[S3]` `[Parameter Store]` - `CODE DEPLOY` - `EC2`

1. 유저가 소스 수정 후 저장소 `GITHUB` 에 `PUSH` 하게되면 `PIPELINE` 에서 트리거를 인식하여 이벤트를 실행
2. 파이프라인 트리거가 실행되면 `Amazone SNS` 를 이용한 Approval 스테이지를 진행
   - 구독중인 Email 로 해당 스테이지의 진행 여부 (승낙 / 거절) 메일을 전송
3. 소스 코드를 `CODE BUILD` 에서 빌드하고, 아티팩트를 `S3` 에 저장
   - 애플리케이션 빌드는 `Docker Image` 로 빌드하여 `ECR` 에 업로드
     - `buildspec.yml` 파일 환경변수는 `CODE BUILD` 생성시 설정
     - 컨테이너 이미지는 `Multi-stage build` 적용하여 필요없는 빌드환경을 제거
   - 배포과정에 사용될 `artifacts` 파일들을 `S3 버킷`에 저장
4. 빌드가 성공하면 `CODE DEPLOY` 에서 아티팩트 `"appspec.yml"` 파일을 확인하여 배포를 실행
   - 스크립트에서 사용되는 환경변수는  `AWS Parameters Store` 사용하여 값을 구성
   - 스크립트 내용에 따라 `ECR` 에서 이미지를 빌드하고 `"docker-compose.yml"` 파일을 실행
5. 해당 EC2 에 배포가 완료되면 애플리케이션 실행


---
### CODE BUILD 진행

CodeBuild가 CI/CD 파이프라인에서 담당할 과정은 다음과 같다  
- GitHub에 있는 Spring 프로젝트를 Pull
- Sring 프로젝트를 빌드
- JAR 파일을 Docker Image로 빌드
- Docker Image를 ECR에 푸시

<br/> 

##### Configuration
![image](https://github.com/26seung/instagram-jsp/assets/79305451/8a2725b1-54af-4e0c-94c3-cfdba73cb2c6)
- CodeBuild 프로젝트의 이름을 지정하고 선택적으로 설명을 입력
- 빌드 배지 - 빌드에 대한 고유한 버전을 식별하기 위해 사용되며, 일반적으로 Git 커밋 해시 또는 버전과 관련된 정보를 포함한다.
- 동시 빌드 제한 활성화 - CodeBuild 프로젝트에서 동시에 실행될 수 있는 최대 빌드 수를 제한하는 설정.  

<br/> 

##### Source
<img width="796" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/e0a9ecd7-fd4d-424e-8433-c217c52a2761">

- 소스 공급자 - 빌드할 프로젝트가 저장되어있는 소스 공급자를 선택
- 리포지토리 - 자신 계정의 리포지토리와 연결하려면 '내 GitHub' 계정의 리포지토리' 선택
- GitHub 리포지토리 - 드롭다운 항목에서 빌드를 수행할 리포지토리 주소 선택
- 연결 상태 - CodeBuild 프로젝트에 GitHub에 접근할 권한을 주기 위해 계정 정보를 통해 연결
- 소스 버전 - 빌드할 리포지토리의 브랜치명 지정
- Git clone 깊이 - 리포지토리 클론 시 가져올 커밋의 깊이를 지정. 1 지정 시 최신 커밋 1개만 가져온다
 
`CHECK` - 웹훅 체크박스는 해제해야 한다. `PIPELINE` 진행시 웹훅을 적용하기 때문에 여기서 적용하면 푸시할 때 빌드가 두 번 돌아가게 된다. 

<br/> 

##### Environment
<img width="802" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/55eaa601-69a3-4b00-857d-c9237ac47bf3">

- 환경 이미지 - 관리형 이미지는 AWS 관리하고 제공하는 사전 구성된 빌드 환경 이미지로 빌드 도구와 라이브러리가 미리 설치되어 있다. 사용자 지정 이미지는 사용자가 빌드 환경을 직접 구성해야한다. 특별한 요구사항이나 환경이 필요한 경우가 아니면 관리형 이미지를 선택
- 운영 체제 - Amazon Linux2, Ubuntu 두 운영체제간의 차이는 크지 않으며 다른 패키지 관리자를 사용하는 정도이다. 선호도에 맞게 선택.
- 런타임 - CodeBuild 프로젝트에서 사용하는 빌드 환경 설정
- 이미지 - 소프트웨어 도구들을 포함하고 있는 이미지 선택
- 환경 유형 - 사용할 빌드 리소스 선택. 일반적인 소프트웨어 개발 및 빌드 작업 시 Linux, 머신 러닝, 3D 그래픽 렌더링 등 작업 시 Linux GPU
- 권한이 있음 - 도커 이미지를 빌드하려면 체크
- 서비스 역할 - CodeBuild 프로젝트에 적용할 IAM 역할 부여  
- 추가 구성 - 제한 시간, 인증서, VPC, 컴퓨팅 유형, `환경 변수`, 파일 시스템 등을 설정
  
CHECK : 이미지 선택시 `x86_64` 확인 필요, `aarch64` 경우 이미지 빌드시 오류 

<img width="730" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/0d436608-131e-45fe-9289-7af3b1929df3">

<br/> 

##### Buildspec
<img width="797" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/d045f1f7-340d-4156-9284-231d57d0faf7">

- Buildspec
    - 빌드 사양 - codeBuild 프로젝트가 수행될 명령어를 파일로 저장할지 프로젝트에 직접 입력할지 선택.
    - Buildspec 이름 - buildspec을 작성할 파일의 이름을 지정, 선택하지 않으면 buildspec.yml
- 아티팩트 
  - 빌드 결과물을 출력할 유형 및 구성을 선택, ECR을 통해 이미지를 저장하면 아티팩트 없음 선택해도 무관한다.
  - 파일들은 `S3버킷`에 저장되며, `CODE DEPLOY`에서 배포과정에 사용 된다.
  - `PIPELINE` 생성시 아티팩트 설정을 진행하여 건너뛴다.

##### log

<img width="796" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/ba995052-2d35-41da-9d18-068ccb7ac07a">



---

오류 : exec /usr/local/openjdk-11/bin/java: exec format error

#### Ubuntu Server에 CodeDeploy 에이전트를 설치
```
sudo apt update -y
sudo apt install ruby-full -y
sudo apt install wget
cd /home/ubuntu
wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
```
서비스 실행중인지 확인 : sudo service codedeploy-agent status
sudo service codedeploy-agent start
sudo service codedeploy-agent status

---

#### 시행착오 겪은 Error01

- `case` : codeDeploy 사용을 위해 codedeploy-agent 를 설치 하였고 IAM 역활등을 부여하였음에도 동작하지 않는 현상
- `원인` : 사용 EC2 에 이전 학습목적 으로 등록하였던 `AWS 자격증명` 구성이 새로 부여한 `IAM 정책연결`이 충돌하여 `codedeploy-agent`가 동작하지 않았음 
- `오류문구` : ```ERROR [codedeploy-agent(671)]: InstanceAgent::Plugins::CodeDeployPlugin::CommandPoller: Cannot reach InstanceService: Aws::CodeDeployCommand::Errors::AccessDeniedException - Aws::CodeDeployCommand::Errors::AccessDeniedException```
- `해결` : 인스턴스에 저장되어 있는 AWS 자격증명 관련 파일을 삭제하면 충돌이 해결된다
     ```
     AWS 자격증명 파일 삭제
     $ sudo rm -rf /root/.aws/credentials
      
     codedeploy-agent 재시작
     $ sudo systemctl restart codedeploy-agent
     ```

#### 시행착오 겪은 Error02

- `case` : codeDeploy 배포과정중에 `appspec.yml` 에서 설정한 destination 경로의 EC2 에 파일이 이동되지 않는 문제
- `원인` : `appspec.yml` 파일의 `hooks`의 이벤트 생명주기 파악 미숙
  ```
  - ApplicationStop - 새 버전을 배포하기 전에 이전 버전의 애플리케이션을 중지하는 단계
  - BeforeInstall - 애플리케이션이 복사되기 전에 실행하는 단계. 여기서 종속성을 설치하거나 기타 사전 설지 작업을 수행한다.
  - Install - 애플리케이션 파일이 인스턴스로 복사되는 단계
  - AfterInstall - 파일이 복사된 후 실행하는 단계
  - ApplicationStart - 애플리케이션을 시작하는 단계
  - ValidateService - 애플리케이션 배포를 검증하는 단계
    ```
- `해결` : `EC2`로의 파일 이동은 `Install`단계 이후 복사되기에, 파일이 필요한 스크립트는 `AfterInstall` 이후 단계에서 구성 
  
###### EC2/온프레미스 배포용 AppSpec 파일 예제
```
version: 0.0
os: linux
files:
  - source: Config/config.txt
    destination: /webapps/Config
  - source: source
    destination: /webapps/myApp
hooks:
  BeforeInstall:
    - location: Scripts/UnzipResourceBundle.sh
    - location: Scripts/UnzipDataBundle.sh
  AfterInstall:
    - location: Scripts/RunResourceTests.sh
      timeout: 180
  ApplicationStart:
    - location: Scripts/RunFunctionalTests.sh
      timeout: 3600
  ValidateService:
    - location: Scripts/MonitorService.sh
      timeout: 3600
      runas: codedeployuser
```
다음은 배포 중 이벤트의 순서입니다.

1. Scripts/UnzipResourceBundle.sh에 있는 스크립트를 실행합니다.
2. 이전 스크립트가 종료 코드 0(성공)을 반환한 경우 이 스크립트를 Scripts/UnzipDataBundle.sh에서 실행합니다.
3. Config/config.txt 경로의 파일을 /webapps/Config/config.txt 경로로 복사합니다.
4. source 디렉터리의 파일을 /webapps/myApp 디렉터리로 모두 복사합니다.
5. Scripts/RunResourceTests.sh에 있는 스크립트를 실행합니다. 제한 시간은 180초(3분)입니다.
6. Scripts/RunFunctionalTests.sh에 있는 스크립트를 실행합니다. 제한 시간은 3600초(1시간)입니다.
7. Scripts/MonitorService.sh에 있는 스크립트를 사용자 codedeploy로 실행합니다. 제한 시간은 3600초(1시간)입니다.

---


---

#### Parameter Store

>`aws ssm get-parameter --name /account/config/region --with-decryption` 사용시 결과값
<img width="904" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/33eaf9ce-ac9e-4125-a87e-ca2affeff6b2">

- `aws ssm get-parameter` 명령어를 통해 파라미터를 조회 할 수 있다.
- `--name` 태그를 이용하여 특정 값을 가져올 수 있다.
- 파라미터 유형이 문자열 값은 상관 없지만 보안 문자열을 사용시에는 `--with-decryption`를 통해 복호화된 값을 볼 수 있다.
- `aws ssm get-parameter --name /**/** --query 'Parameter.Value' --output text --with-decryption`사용하면 값만 추출가능



---

애플리케이션을 이미지로 빌드하여 사용시 꽤 큰 용량의 이미지 크기를 가지게 된다...

기존 도커 이미지 빌드
```dockerfile
# APP
FROM openjdk:11.0-slim

WORKDIR /app
#   현재 경로의 파일들을 "WORKDIR" 위치로 복사
COPY . .
#   권한설정 & jar build
RUN chmod +x ./gradlew
RUN ./gradlew clean build

#  (jar/war) 파일 위치 설정
ENV JAR_FILE=./build/libs/*-SNAPSHOT.war

RUN mv ${JAR_FILE} /app/app.war

ARG JA_SECRET
ENV JASYPT_SECRETE_KEY=${JA_SECRET}
ENV TZ=Asia/Seoul
ENV DB_URL_ADDRESS=mariaDB

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.war"]
```

멀티스테이지 빌드

```dockerfile
# Build Stage
FROM openjdk:11.0-slim AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build

# Final Stage
FROM openjdk:11.0-slim
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.war /app/app.war

ARG JA_SECRET
ENV JASYPT_SECRETE_KEY=${JA_SECRET}
ENV TZ=Asia/Seoul
ENV DB_URL_ADDRESS=mariaDB

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.war"]
```