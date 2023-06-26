# 인스타그램 클론 코딩 프로젝트

---

#### 관련 페이지 이동
- [API Document](md/API.md)
- [AWS Arrchitecture](md/AWS.md)
- [Page 간단 설명](md/page.md)
---

### 개요 

**이 프로젝트는 인스타그램의 기능을 클론 코딩한 웹 애플리케이션입니다.   
개발 목적은 사용자들이 사진을 공유하고 소셜 네트워크로 연결되는 환경을 구축하는 것 입니다.**  
페이지 방문을 원하시면 **[여기로 (https://acm.euseung.shop)](https://acm.euseung.shop)** 이동 하세요. **(Guest 계정 : user, 1234)**


---

### 주요 특징

- **회원 가입과 로그인** : 사용자는 개인 계정을 생성하고 로그인하여 애플리케이션의 기능을 사용할 수 있습니다.
- **프로필 관리** : 사용자는 자신의 프로필 사진과 소개를 업로드하고 관리할 수 있습니다.
- **게시물 작성 및 공유** : 사용자는 사진과 설명을 포함한 게시물을 작성하고 다른 사용자와 공유할 수 있습니다.
- **게시물 좋아요 및 댓글** : 사용자는 다른 사용자의 게시물에 좋아요를 누르고, 댓글을 작성할 수 있습니다.
- **팔로우 기능** : 사용자는 다른 사용자를 팔로우하고, 팔로우하는 사용자의 게시물을 피드에서 확인할 수 있습니다.


- **AWS 배포 구성** : 배포시 Public 이 아닌 Private Subnet 을 이용한 [Arrchitecture](md/AWS.md) 를 구성하여 외부에서의 보안을 강화하였습니다.

---

### 사용된 기술

- 백엔드: Java, Spring Boot, Spring Security, JPA
- 프론트엔드: JavaScript, JSP(JavaServer Pages)
- 데이터베이스: MariaDB
- 배포: AWS, Docker, Git

---
