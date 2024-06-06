# 베이스 이미지 설정
FROM gradle:8.7-jdk17-alpine AS build

WORKDIR /app

# 의존성 파일을 먼저 복사하여 캐시 활용
COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon

COPY . .

RUN gradle build --no-daemon -x test

# 베이스 이미지를 다시 설정하여 경량화
FROM openjdk:17-jdk-alpine

# 빌드된 jar 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java","-jar","/app.jar"]
