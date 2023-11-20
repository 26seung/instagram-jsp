


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

#### 시행착오 겪은 Error 관련

1. **case** : codeDeploy 사용을 위해 codedeploy-agent 를 설치 하고 IAM 역활등을 부여하였음에도 동작하지 않는 현상
   - **원인** : 사용 EC2 에 이전 학습목적 으로 등록하였던 `AWS 자격증명` 구성이 새로 부여한 `IAM 정책연결`이 충돌하여 `codedeploy-agent`가 동작하지 않았음 

오류문구 : 
```ERROR [codedeploy-agent(671)]: InstanceAgent::Plugins::CodeDeployPlugin::CommandPoller: Cannot reach InstanceService: Aws::CodeDeployCommand::Errors::AccessDeniedException - Aws::CodeDeployCommand::Errors::AccessDeniedException```
인스턴스에 기존 AWS 자격 증명 파일이 저장되어 있어 IAM 역활을 지정하여도 인식을 하지 못하는 현상

```
AWS 자격증명 파일 삭제
$ sudo rm -rf /root/.aws/credentials

codedeploy-agent 재시작
$ sudo systemctl restart codedeploy-agent
```

2. **case** : codeDeploy 배포과정중에 `appspec.yml` 에서 설정한 destination 경로의 EC2 에 파일이 이동되지 않는 문제
    - **원인** : `appspec.yml` 파일의 `hooks`의 이벤트 생명주기 파악 미숙


- ApplicationStop - 새 버전을 배포하기 전에 이전 버전의 애플리케이션을 중지하는 단계
- BeforeInstall - 애플리케이션이 복사되기 전에 실행하는 단계. 여기서 종속성을 설치하거나 기타 사전 설지 작업을 수행한다.
- Install - 애플리케이션 파일이 인스턴스로 복사되는 단계
- AfterInstall - 파일이 복사된 후 실행하는 단계
- ApplicationStart - 애플리케이션을 시작하는 단계
- ValidateService - 애플리케이션 배포를 검증하는 단계

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