# REST API DOC

---
- [사용자 API](#사용자-api)
- [구독 API](#구독-api)
- [이미지 API](#이미지-api)
- [댓글 API](#댓글-api)

---

## 사용자 API

### 프로필 사진 변경

인증된 사용자의 프로필 사진을 변경합니다.

- **URL**: `/api/user/{principalId}/profileImageUrl`
- **Method**: PUT
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `principalId` (int): 프로필 사진을 변경할 사용자의 ID
    - `profileImageFile` (MultipartFile): 변경할 프로필 사진 파일
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "프로필사진변경 성공",
                "data": null
            }
            ```


### 구독자 정보 리스트 가져오기

특정 사용자의 구독자 정보 리스트를 가져옵니다.

- **URL**: `/api/user/{pageUserId}/subscribe`
- **Method**: GET
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `pageUserId` (int): 구독자 정보를 가져올 사용자의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "구독자 정보 리스트 가져오기 성공",
                "data": [
                    {
                        // 구독자 정보
                    }
                ]
            }
            ```

### 회원 수정

인증된 사용자의 정보를 수정합니다.

- **URL**: `/api/user/{id}`
- **Method**: PUT
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `id` (int): 수정할 사용자의 ID
    - `userUpdateDto` (UserUpdateDto): 수정할 사용자 정보가 담긴 DTO
    - `bindingResult` (BindingResult): 유효성 검사 결과
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "회원수정완료",
                "data": [
                    {
                        // 구독자 정보
                    }
                ]          
          }
          ```
          

---

API 문서를 작성하기 위해 코드를 살펴보았습니다. 아래는 주어진 코드에 대한 간단한 API 문서 예제입니다.

## 구독 API

### 구독하기
사용자가 다른 사용자를 구독하는 요청을 보냅니다.

- **URL**: `/api/subscribe/{toUserId}`
- **Method**: POST
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `toUserId` (int): 구독 대상 사용자의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "구독하기 성공",
                "data": null
            }
            ```


### 구독 취소하기
사용자가 구독을 취소하는 요청을 보냅니다.

- **URL**: `/api/subscribe/{toUserId}`
- **Method**: DELETE
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `toUserId` (int): 구독 대상 사용자의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "구독하기 성공",
                "data": null
            }
            ```


---

## 이미지 API

### 이미지 스토리 조회

인증된 사용자의 이미지 스토리를 조회합니다.

- **URL**: `/api/image`
- **Method**: GET
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `pageable` (Pageable): 페이지네이션 및 정렬 정보 (기본값: 페이지 크기 3, ID 기준 정렬)
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "성공",
                "data": {
                    // 이미지 스토리 정보 (페이지네이션 적용)
                }
            }
            ```

### 단일 이미지 스토리 조회

인증된 사용자의 단일 이미지 스토리를 조회합니다.

- **URL**: `/api/image/{imageId}`
- **Method**: GET
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `imageId` (int): 조회할 이미지의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "성공",
                "data": {
                    // 단일 이미지 스토리 정보
                }
            }
            ```

### 좋아요

이미지에 좋아요를 추가합니다.

- **URL**: `/api/image/{imageId}/likes`
- **Method**: POST
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `imageId` (int): 좋아요를 추가할 이미지의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 201 (CREATED)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "좋아요성공",
                "data": null
            }
            ```

### 좋아요 취소

이미지에 추가된 좋아요를 취소합니다.

- **URL**: `/api/image/{imageId}/likes`
- **Method**: DELETE
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 매개변수**:
    - `imageId` (int): 좋아요를 취소할 이미지의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "좋아요취소성공",
                "data": null
            }
            ```

---

API 문서를 작성하기 위해 주어진 코드를 살펴보았습니다. 아래는 주어진 코드에 대한 간단한 API 문서 예제입니다.

## 댓글 API

### 댓글 작성

댓글을 작성합니다.

- **URL**: `/api/comment`
- **Method**: POST
- **인증**: 필요 (AuthenticationPrincipal)
- **요청 본문**:
    - `commentDto` (CommentDto): 댓글 정보
        - `content` (String): 댓글 내용
        - `imageId` (int): 댓글이 작성된 이미지의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 201 (CREATED)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "댓글쓰기성공",
                "data": {
                    // 작성된 댓글 정보
                }
            }
            ```

### 댓글 삭제

댓글을 삭제합니다.

- **URL**: `/api/comment/{id}`
- **Method**: DELETE
- **인증**: 필요
- **경로 매개변수**:
    - `id` (int): 삭제할 댓글의 ID
- **응답**:
    - 성공 시:
        - 상태코드: 200 (OK)
        - 응답 본문:
            ```json
            {
                "code": 1,
                "message": "댓글삭제성공",
                "data": null
            }
            ```
          
---
