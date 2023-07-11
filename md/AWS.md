# AWS Architecture

---

### 배포 구성 관련 Architecture 이미지

![aws_architecture1](https://github.com/26seung/instagram-jsp/assets/79305451/06657766-2368-4bf2-99be-c19f573739ca)

---

### AWS 배포 특징

- 웹 서버는 **Private Subnet** 에 구축하여 보안을 강화하고, 데이터베이스 서버를 분리하여 가용성 확보.
- 인스턴스의 접근을 관리하기 위해 **Bastion Host** 를 사용하여 Private EC2에 접근하며, 접근이 필요 없는 경우에는 인스턴스를 중지시켜 보안을 강화.
- VPC 내에서의 인터넷 통신은 **NAT Instance**를 생성하여 가능하도록 설정.
- 외부 사용자가 접근 시에는 **ACM 과 ALB** 를 사용하여 `ssl/tls` HTTPS(443) 통신을 제공히고, Private 서버에는 **ALB**를 사용하여 HTTP(80) 연결 되도록 구성.
- 시스템의 가용성을 높이기 위해 추가적인 가용 영역(AZ)을 구성하여 장애 발생 시에도 서비스 지속성을 보장.


---