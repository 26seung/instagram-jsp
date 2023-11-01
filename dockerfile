 # APP
FROM openjdk:11.0-slim

WORKDIR /app
#   현재 경로의 파일들을 "WORKDIR" 위치로 복사
COPY . .
#   권한설정 & jar build
RUN chmod +x ./gradlew
RUN ./gradlew clean build

#  (jar/war) 파일 위치 설정
ENV JAR_FILE=./build/libs/*-SNAPSHOT.war

RUN mv ${JAR_FILE} /app/app.war

ENV TZ=Asia/Seoul
ENV JASYPT_SECRETE_KEY=euseung
ENV DB_URL_ADDRESS=mariaDB

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.war"]


 #  수행 명령어 정리
 # 1. war 파일 생성 : ./gradlew clean build
 # 2. docker 이미지 빌드 : docker build -t myapp .
 # 3. docker 컨테이너 실행 : docker run --name myapp -v /workspace/backup/:/workspace/ -p 80:8080 -d myapp

# docker run --name myapp p 8080:8080 -d myapp