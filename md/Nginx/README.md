## Nginx 설정

```
upstream backend {
        server backend:8080;
}

location /api/ {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';

            proxy_redirect     off;
            proxy_set_header   Host $host;
            proxy_set_header   X-Real-IP $remote_addr;
            proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header   X-Forwarded-Host $server_name;

}
```

- `location /api/ { ... }` :
  - 해당 `/api/` 경로에 대한 설정을 정의합니다. 즉, 이 설정은 웹 클라이언트가 /api/로 시작하는 요청을 받았을 때 동작합니다.
- `proxy_pass http://backend;` :
  - `/api/`로 들어오는 요청은 `http://backend`로 프록시됩니다. 이는 실제로는 다른 서버로의 요청을 의미합니다. backend는 Nginx 설정 파일의 다른 부분에서 정의된 업스트림 서버 블록일 것입니다.

---

- `proxy_redirect off;` : 
  - 이 지시어는 프록시 응답에서 오는 리다이렉트 헤더를 무시하도록 설정합니다.
  - 유지보수성과 투명성 유지 : 
    - 백엔드 서버에서 오는 리다이렉트를 그대로 전달하면, 프록시 서버는 단순히 전달만 하는 역할을 하기 때문에 프록시 서버의 설정을 변경하지 않고도 백엔드 서버에서 발생하는 리다이렉트를 그대로 유지할 수 있습니다.


- `proxy_set_header Host $host;`:
  - 이 지시어는 프록시 서버로 전송되는 요청 헤더의 Host 필드를 원본 요청의 Host 필드와 동일하게 설정합니다.


- `proxy_set_header   X-Real-IP $remote_addr;` : 
  - 이 지시어는 프록시 서버로 전달되는 요청 헤더의 `X-Real-IP`를 클라이언트의 실제 IP 주소로 설정합니다.
    - `X-Real-IP`
      - 보통 웹 서버나 프록시 서버에서 클라이언트의 원본 IP 주소를 식별하기 위해 사용되는 HTTP 헤더 중 하나 입니다.
      - `Nginx`가 받은 요청 중에서 클라이언트의 실제 IP 주소를 `X-Real-IP`라는 헤더에 담아서 백엔드 서버로 전달하는 역할
    - `$remote_addr`
      - `Nginx`에서 사용되는 변수로, 현재 요청을 보내는 클라이언트의 IP 주소를 나타냅니다. 이 변수는 Nginx가 받은 실제 TCP 연결의 상대방 주소를 나타냅니다.
      - `proxy_set_header X-Real-IP $remote_addr;`에서 `$remote_addr`는 현재 요청을 보내는 클라이언트의 IP 주소를 의미합니다. 이 정보를 `X-Real-IP` 헤더에 담아서 백엔드 서버로 전달하면, 백엔드 서버는 이 헤더를 통해 실제 클라이언트의 IP 주소를 확인할 수 있습니다.


- `proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;` :
  - 이 지시어는 프록시 서버로 전달되는 요청 헤더의 X-Forwarded-For를 클라이언트의 원본 IP 주소로 설정합니다. 이 헤더는 중간 프록시를 통과한 클라이언트의 IP 주소 목록을 나타냅니다.
    - 일반적으로, HTTP 요청이 클라이언트에서 웹 서버로 전달될 때, X-Forwarded-For 헤더는 다음과 같은 형태를 가진다.
    - ```
      makefileCopy code
      X-Forwarded-For: client, proxy1, proxy2
      ```
      - `client` : 실제 클라이언트의 IP 주소
      - `proxy1`, `proxy2` : 클라이언트와 서버 사이를 거치는 각각의 프록시 서버의 IP 주소
      

- `proxy_set_header   X-Forwarded-Host $server_name;`
  - 이 지시어는 프록시 서버로 전달되는 요청 헤더의 X-Forwarded-Host를 Nginx 서버의 이름으로 설정합니다.
    -  클라이언트가 `example.com` 도메인으로 요청을 보내면, `X-Forwarded-Host` 헤더는 `example.com` 값을 가진다.

---

##### `X-` 는 뭐냐?
- `X`로 시작하는 HTTP 헤더들은 보통 사용자가 정의한 사용자 지정 헤더를 나타냅니다. 이러한 사용자 정의 헤더들은 표준 HTTP 헤더가 아니지만, 특정 상황에서 유용한 정보를 전달하기 위해 사용됩니다.
- `X-Forwarded-Host`나 `X-Real-IP`와 같은 헤더에서의 `X`는 `"예외"` 또는 `"사용자 정의"`를 의미합니다. 즉, 이 헤더들은 표준 HTTP 헤더가 아니지만, 특정 프록시 서버나 애플리케이션에서 추가적인 정보를 전달하기 위해 사용되는 사용자 정의 헤더입니다.
- `X`는 예전에 사용자 정의 헤더를 나타내기 위해 자주 사용되었던 접두어로, 현대의 권장 사항에 따라 새로운 헤더에서는 생략하는 경우도 있습니다.
