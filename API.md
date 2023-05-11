### REST API

---

1. 유저 정보 관련 API
 - 프로필 이미지 변경
   - URL: /api/user/{principalId}/profileImageUrl
   - Method: PUT
   - Request Body: MultipartFile profileImageFile
   - Request Params:
     - principalId: 로그인한 사용자 ID
   - Response Body:
     - data: null
   - Response Status:
     - 200 OK: 프로필 이미지 변경 성공
     - 400 Bad Request: 잘못된 요청
     - 401 Unauthorized: 권한 없음
     - 404 Not Found: 해당 유저 정보 없음
     - 500 Internal Server Error: 서버 오류 발생
  

 - 구독자 정보 리스트 가져오기
   - URL: /api/user/{pageUserId}/subscribe
   - Method: GET
   - Request Params:
     - pageUserId: 유저 정보를 가져올 페이지의 ID
   - Response Body:
     - data: List<SubscribeDto>
     - id: 구독 정보 ID
     - username: 유저 이름
     - profileImageUrl: 프로필 이미지 URL
     - subscribeState: 구독 상태 (구독중, 구독취소, 나의 페이지)
     - equalUserState: 동일한 유저 여부 (동일 유저, 다른 유저)
   - Response Status:
     - 200 OK: 구독자 정보 리스트 가져오기 성공
     - 400 Bad Request: 잘못된 요청
     - 500 Internal Server Error: 서버 오류 발생
   

- 회원 정보 수정
  - URL: /api/user/{id}
  - Method: PUT
  - Request Body: UserUpdateDto
    - password: 비밀번호
    - name: 이름
    - website: 웹사이트
    - bio: 자기소개
    - phone: 전화번호
    - gender: 성별
  - Request Params:
    - id: 수정할 유저 정보 ID
  - Response Body:
    - data: User
    - id: 유저 ID
    - username: 유저 이름
    - password: 비밀번호
    - name: 이름
    - website: 웹사이트
    - bio: 자기소개
    - email: 이메일
    - phone: 전화번호
    - gender: 성별
    - role: 권한 (USER, ADMIN)
    - createDate: 생성일자
    - msg: 메시지
  - Response Status:
    - 200 OK: 회원 정보 수정 성공
    - 400 Bad Request: 잘못된 요청
    - 404 Not Found: 해당 유저 정보 없음
    - 500 Internal Server Error: 서버 오류 발생