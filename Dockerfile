# 1단계: 빌드 전용 환경 - Gradle로 실제 jar 파일을 만드는 단계
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Gradle 캐시를 활용하기 위해, 소스 코드보다 의존성 관련 파일을 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon || true

# 나머지 소스 코드 복사 후 빌드 (테스트는 이미지 빌드 시 생략)
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행 전용 환경 - 빌드 도구 없이, 가볍게 jar 파일만 실행
FROM eclipse-temurin:21-jre AS run

WORKDIR /app

# 1단계에서 만들어진 jar 파일만 가져옴 (빌드 도구, 소스코드는 최종 이미지에 안 남음)
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]