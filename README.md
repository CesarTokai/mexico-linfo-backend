# Mexico Lindo Tours Backend

Sistema de gestión de viajes, camionetas y cuentas — Urvan NV350.

## Requisitos

- **Java 17+**
- **Maven 3.8+**
- **MySQL 8+**

## Setup

### 1. Base de datos

```bash
mysql -u root -p < schema.sql
```

Configura credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

### 2. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La app arranca en `http://localhost:8080`.

### 3. Crear primer usuario (ADMIN)

```bash
curl -X POST "http://localhost:8080/auth/crear-usuario?nombre=Admin&correo=admin@mexicolindo.com&password=admin123&rol=ADMIN"
```

### 4. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@mexicolindo.com","password":"admin123"}'
```

Respuesta:
```json
{
  "token": "eyJhbGc...",
  "nombre": "Admin",
  "correo": "admin@mexicolindo.com",
  "rol": "ADMIN"
}
```

## Estructura

```
src/main/java/com/mexicolindotours/
  config/       Configuración (Security, CORS)
  security/     JWT (JwtTokenProvider)
  model/        Entidades JPA
  repository/   Acceso a datos
  service/      Lógica de negocio
  controller/   Endpoints REST
  dto/          Objetos entrada/salida
```

## Documentación

- `CLAUDE.md` — instrucciones de construcción
- `Mexico_Lindo_Tours_Sistema.md` — especificación técnica completa
- `schema.sql` — esquema MySQL v2
