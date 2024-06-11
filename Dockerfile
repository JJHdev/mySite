# 베이스 이미지 선택 (예: AdoptOpenJDK 17)
FROM openjdk:17-alpine

# 컨테이너 내에서 작업 디렉토리 설정
WORKDIR /app

# 호스트 파일시스템의 현재 디렉토리의 모든 파일을 컨테이너 내의 /app 디렉토리로 복사
COPY . .

# Gradle Wrapper를 통해 애플리케이션 빌드
RUN ./gradlew build

# 컨테이너가 런타임 시에 사용할 포트 노출
EXPOSE 8080

# 애플리케이션 실행
CMD ["java", "-jar", "build/libs/myapp.jar"]