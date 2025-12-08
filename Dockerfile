# Multi-stage build para optimizar el tamaño de la imagen
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachean si no cambia el pom.xml)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar y empaquetar la aplicación
RUN mvn clean package -DskipTests

# Imagen final más liviana
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar wget para healthcheck (antes de cambiar de usuario)
RUN apk add --no-cache wget

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar el JAR desde la etapa de build (como root)
COPY --from=build /app/target/*.jar app.jar

# Cambiar ownership del JAR al usuario spring
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring:spring

# Exponer el puerto de la aplicación
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

