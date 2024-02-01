# Используем базовый образ с Java 17
FROM openjdk:17-alpine

# Установка рабочей директории внутри контейнера
WORKDIR /app

# Копирование JAR-файла внутрь контейнера

COPY out/artifaсts/Sal_jar/Sale.jar .
#COPY D:/Projects/Sale/out/artifacts/Sale_jar/Sale.jar .

# Запуск приложения при старте контейнера
CMD ["java", "-jar", "Sale.jar"]
