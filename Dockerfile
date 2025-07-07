# Etapa 1: Construcción del JAR
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

# Copiamos el POM y las fuentes
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto (sin correr tests)
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera con JAR listo para ejecutarse
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiamos el .jar desde el contenedor anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto (ajústalo si usas uno distinto)
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
