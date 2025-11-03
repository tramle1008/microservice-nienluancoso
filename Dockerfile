# Dùng JDK 21 vì bạn đã cài version này
FROM eclipse-temurin:21-jdk

# Thư mục làm việc trong container
WORKDIR /app

# Copy file JAR đã build từ Maven/Gradle
COPY target/*.jar app.jar

# Expose port của ứng dụng
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
