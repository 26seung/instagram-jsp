## AWS CodeBuild 사용

---

> AWs CodeBuild란 클라우드의 완전 관리형 빌드 서비스로, 소스코드를 컴파일하고 단위테스트를 실행하며 배포할 준비가 완료된 아티팩트 파일을 생성한다. CodeBuild는 빌드서버를 프로비저닝 및 관리할 필요가 없으며 Maven, Gradle과 같은 널리 사용되는 프로그래밍 언어 및 도구에 맞게 사전 패키지된 빌드 환경을 제공한다.

AWS CodeBuild는 다음과 같은 장점이 있다.

- 빌드 서버를 직접 설정(패치 및 업그레이드 등)하고 관리할 필요가 없다.
- 사전 빌드된 빌드환경을 제공하기 때문에 빌드스크립트 선택 및 작성 후 시작 가능하다.
- 빌드요구사항 충족을 위한 확장/축소가 가능하며 `사용한 빌드 시간만큼만 비용을 지불`한다

---

### CODE BUILD 진행
<br />
CodeBuild가 CI/CD 파이프라인에서 담당할 과정은 다음과 같다

- GitHub에 있는 Spring 프로젝트를 Pull
- Sring 프로젝트를 빌드
- JAR 파일을 Docker Image로 빌드
- Docker Image를 ECR에 푸시


---
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

