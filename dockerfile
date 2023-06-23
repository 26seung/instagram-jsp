 # APP
FROM openjdk:11.0-slim

#  (jar/war) 파일 위치 설정
ARG JAR_FILE=./build/libs/*-SNAPSHOT.war

COPY ${JAR_FILE} app.war

ENV TZ=Asia/Seoul
ENV JASYPT_SECRETE_KEY=euseung
ENV IP_URL_ADDRESS=192.168.0.18

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.war"]


 #  수행 명령어 정리
 # 1. war 파일 생성 : ./gradlew clean build
 # 2. docker 이미지 빌드 : docker build -t myapp .
 # 3. docker 컨테이너 실행 : docker run --name myapp -v /workspace/backup/:/workspace/ -p 80:8080 -d myapp