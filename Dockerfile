# ---------- build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Las dependencias se cachean aparte del codigo: cambiar una clase no
# obliga a volver a bajar medio Maven.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- runtime ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# curl es para el healthcheck; la imagen JRE no lo trae.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# No correr como root.
RUN groupadd -r spring && useradd -r -g spring spring

COPY --from=build /build/target/*.jar app.jar

# Las imagenes subidas viven aqui; se monta como volumen para que
# sobrevivan a los redespliegues.
RUN mkdir -p /app/uploads && chown -R spring:spring /app

USER spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://localhost:8080/publico/filtros || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
