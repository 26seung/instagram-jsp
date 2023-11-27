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

# INDEX

1. [CODE BUILD](#code-build-진행)
2. [CODE DEPLOY](#code-deploy-진행)
3. [CODE PIPELINE](#code-pipeline-진행)

- [오류 해결1](#시행착오-겪은-error01)
- [오류 해결2](#시행착오-겪은-error02)

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

이후 `프로젝트 생성`을 누르고 아래와 같은 작업을 추가 해주면 된다

<img width="1745" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/67a2aae2-4143-4b36-88a0-1f1b96a2122e">

##### buildspec.yml

```yaml
version: 0.2

phases:
  pre_build:
    commands:
      - echo Logging into Amazon ECR...
      - aws --version
      - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com
      - REPOSITORY_URI=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME
  build:
    commands:
      - echo Build started on `date`
      - echo Building Docker image...
      - docker build -t was . --build-arg JA_SECRET="$JA_SECRET"
  post_build:
    commands:
      - echo Build completed on `date`
      - echo Pushing Docker image to ECR...
      - docker tag was:latest $REPOSITORY_URI:latest
      - docker push $REPOSITORY_URI:latest
artifacts:
  files:
    - "appspec.yml"
    - "scripts/*"
    - "docker-compose.yml"
    - "nginx/conf.d/nginx.conf"

```
- version : 0.2 권장
- phases.pre_build.commands : 빌드 전 수행되는 명령어. 
  - 해당 이미지를 업로드 하기위한 `ECR`에 로그인
- phases.build step.commands : 빌드 시 수행되는 명령어.
  - 도커를 이용하여 해당 애플리케이션을 이미지 빌드.
- phases.post_build step.commands : 빌드 후 수행되는 명령어. 
  - 빌드된 이미지의 공통 tag를 지정
  - 도커 이미지를 `ECR` 주소에 업로드
- artifacts : `S3버킷`에 전달할 빌드 결과파일. 
  - [discard-paths: yes] : 해당 옵션 사용시 path(build/libs)는 무시되고 파일명으로만 업로드.
- cache.paths : 이곳의 파일을 S3 cache에 등록.


---

### CODE DEPLOY 진행

CodeDeploy가 CI/CD 파이프라인에서 담당할 과정은 다음과 같다

- CodeBuild 빌드 작업이 완료되면 EC2에 접속하여 `CodeDeploy agent` 와 통신
- `AppSpec.yml` 파일에 작성된 명령을 순차적 실행
- `ECR`에 저장된 도커 이미지를 풀
- 풀 받은 이미지를 실행
- 실행된 프로그램이 게이트웨이에 인식이 되었는지 확인 후 이전 버전의 프로그램 종료 및 삭제

<br / >

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
  - CodeDeployDefault.OneAtTime - 한 번에 하나의 인스턴스에만 배포 수행. 배포 중에 서비스 중단이 없도록할 수 있지만 배포 시간이 길어질 수 있다.
  - CodeDeployDefault.HalfAtTime - 배포 단계에서 사용 가능한 인스턴스의 절반에 배포 수행.
  - CodeDeployDefault.AllAtOnce - 모든 인스턴스에 동시에 배포 수행. 가장 빠르지만 배포 중 모든 서비스가 중단될 수 있다.

<br/> 

##### 배포만들기
<img width="775" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/69c8089f-8cb0-4da3-90ef-3f5e9190477c">

- `PIPELINE` 사용시 설정 하므로 만들 필요 없음
- `CODEDEPLOY` 단일로 실행시에는 생성하여 테스트 가능

---

### code-pipeline-진행

##### 파이프라인 생성
<img width="795" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/6450e013-714a-45a3-88cc-aa28762721b4">
<img width="789" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/80973a45-693a-4d57-8d1c-8be89e612611">

- 이름 생성시 자동으로 `IAM 역활` 부여하여 생성된다.
- [고급 설정]에서는 아티팩트 저장소를 지정한다
  - [기본 위치]를 선택할 경우 새로운 S3 버킷을 자동으로 생성.
  - [사용자 지정 위치]를 선택할 경우 미리 만들어 놓은 버킷을 선택할 수 있다. 
- 해당 아티팩트 경로로 `buildspec.yml` 에서 설정 하였던 `appspec.yml`관련 파일들이 저장된다.

<br />

##### 파이프라인 (소스 스테이지 추가)
<img width="631" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/4155c7a0-edfa-4c11-9f87-14f804637eb8">

- 소스 공급자
  - GitHub(버전 1) - GitHub API v3를 사용하는 이전에 사용되던 방식으로 인증 토큰을 사용해 통신한다. 대량의 변경 사항을 처리하는데 어려움이 있을 수 있고 GitHub 액션을 사용할 수 없다. `현재는 사용을 권장하지 않는다`.
  - GitHub(버전 2) - GitHub API v4를 사용하고 개별적인 사용자 권한을 통해 GitHub과 연결된다. 페이징이나 정렬과 같은 기능이 추가되었으며 대량의 변경 사항 처리가 향상되었고. GitHub액션을 사용할 수 있다. `해당 버전 사용을 권장`.
- 연결 - 기존에 구성한 기존 연결이 있으면 선택하고 아니라면 'GitHub에 연결' 버튼을 통해 연결할 리포지토리에 대한 연결을 진행한다.
- 리포지토리 이름, 브랜치 이름 - 변경 감지 및 소스 공급을 할 리포지토리와 브랜치를 선택
- 파이프라인 트리거 - 체크 시 소스코드 변경을 감지하여 파이프라인이 시작된다.
- 출력 아티팩트 형식 - 파이프라인 실행 중 생성된 아티팩트의 형식을 지정. 파이프라인 결과와는 무방하고, 아티팩트의 전송 방식이나 구성을 변경함에 따라 선택이 바뀔 수 있다.

<br / >

##### 파이프라인 (빌드 스테이지 추가)
<img width="628" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/d4795baf-f907-472a-b71e-b13a6dd3b33a">

- 이전에 생성한 CodeBuild의 프로젝트를 선택.
- 빌드 유형
  - 단일 빌드 - 단일 CodeBuild 빌드 작업을 생성하고 실행한다. 빌드 단계가 실행될 때마다 하나의 빌드 작업이 생성되며, 해당 작업은 단일 개체로 처리된다. 빌드 작업이 직렬로 처리되며, 각 단계의 이전 빌드가 성공적으로 완료된 후 다음 빌드 단계가 실행된다.
  - 배치 빌드 - 여러 개의 CodeBuild 빌드 작업을 배치로 생성하고 실행한다. 여러 빌드 작업을 동시에 실행하고, 병렬로 진행될 수 있다. 빌드 효율성을 높이고 시간을 단축시킬 수 있다.

<br />

##### 파이프라인 (배포 스테이지 추가)
<img width="633" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/3a6a51e9-4184-4f09-acf8-45cd4e6d604f">

- 이전에 생성한 CodeDeploy의 애플리케이션 및 배포그룹을 선택.

<br />

##### 파이프라인 (편집 - 스테이지 추가 - 수동승인)
<img width="1291" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/4af06d2d-e36b-482f-8627-9f43923adc28">

- 완성된 파이프라인에 편집을 통해 추가적인 스테이지를 구성할 수 있다.
- 해당 파이프라인 실행시 `SNS` 를 활용한 이메일 트리거를 통해 진행여부를 `승낙/거부` 구성 하였다.

<br />

##### 파이프라인 
<img width="405" alt="image" src="https://github.com/26seung/instagram-jsp/assets/79305451/46cfdf52-eb41-432e-8c15-112fc3429a58">

- 완성된 `파이프라인` 구성도이다.

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