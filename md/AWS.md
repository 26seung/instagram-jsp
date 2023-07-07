# AWS Architecture

---

### 배포 구성 관련 Architecture 이미지

![aws_architecture1](https://github.com/26seung/instagram-jsp/assets/79305451/06657766-2368-4bf2-99be-c19f573739ca)

---

### AWS 배포 특징

- 웹 서버는 **Private Subnet** 에 구축하여 보안을 강화, 데이터베이스 서버를 분리하여 가용성 확보.
- Private EC2 접근은 **Bastion Host** 를 통해 접근하며, 접근이 필요없는 평상시는 인스턴스 중지.
- 인터넷 연결은 **NAT Instance** 를 생성하여 해당 VPC 내에서 인터넷 통신.
- 외부 사용자가 접근 시에는 **ACM 과 ALB** 를 사용하여 HTTPS(443) 통신히고, Private 서버에는 HTTP(80) 연결 되도록 구성.
- 가용성을 위하여 AZ 추가 구성


- 외부 사용자가 접근 시에는 **ALB** 를 사용하여 Private 서버에 HTTP(80) 연결 되도록 구성
- **ACM 과 ALB** 를 이용하여 HTTPS(443) 통신 하도록 도메인을 구성


---