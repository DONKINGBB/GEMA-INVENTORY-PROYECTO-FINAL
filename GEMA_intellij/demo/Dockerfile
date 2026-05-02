# Fase 1: Compilación
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copiar el archivo pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Fase 2: Ejecución
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copiar el archivo JAR generado
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto 8080
EXPOSE 8080

# Comando para iniciar la aplicación con preferencia de IPv4
ENTRYPOINT ["java", "-jar", "app.jar"]
