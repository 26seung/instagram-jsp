## AWS CodePipeline

---

> AWS CodePipeline은 빠르고 안정적인 애플리케이션 및 인프라 업데이트를 위한 CI/CD 서비스로 빌드, 테스트, 배포 서비스를 자동화 할 수 있다. 사용자가 정의한 릴리즈 프로세스 모델에 따라 코드가 변경될 때마다 코드를 빌드, 테스트 및 배포를 수행한다. AWS CodePipeline은 CodeCommit/CodeBuild/CodeDeploy와 같이 프로비저닝 또는 설정할 서버가 필요하지 않다.
요금은 각 활성 파이프라인 당 월 1USD이며(첫 30일은 무료), 만약 CodeCommit, CodeBuild를 같이 사용하더라도 요금과는 별도로 산정된다.


---


### CODE PIPELINE 진행

<br />

---

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

<br />

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
