# Используем базовый образ с установленной Java (например, OpenJDK)
FROM openjdk:16-jre-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY out/artifacts/Sale_jar/Sale.jar app.jar

# Команда для запуска приложения при старте контейнера
CMD ["java", "-jar", "app.jar"]