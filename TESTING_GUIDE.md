# Testing Guide — Mexico Lindo Tours

## 1. Orden de Carga (IMPORTANTE)

**Paso 1 — Arrancar backend primero.** Hibernate (`ddl-auto=update`) agrega columnas que no están en schema.sql: `pago.metodo`, `gasto.fecha`, `gasto.notas`. Si cargas el SQL antes, fallan los INSERT.

**Paso 2 — Crear usuarios via API** (el hash BCrypt lo genera la app, no se inserta por SQL):

```bash
curl -X POST "http://localhost:8080/auth/crear-usuario?nombre=Admin&correo=admin@mexicolindo.com&password=admin123&rol=ADMIN"
curl -X POST "http://localhost:8080/auth/crear-usuario?nombre=Gestor&correo=gestor@mexicolindo.com&password=admin123&rol=GESTOR"
```

**Paso 3 — Cargar datos de prueba:**

### Opción A: MySQL Workbench
1. Conecta a BD `mexico_lindo_tours`
2. Abre archivo: `data-prueba.sql`
3. Ejecuta (Ctrl+Shift+Enter)

### Opción B: Línea de comandos
```bash
mysql -u root -p mexico_lindo_tours < data-prueba.sql
```

---

## 2. Verificar Datos

```sql
SELECT COUNT(*) as total_usuarios FROM usuario;   -- 2
SELECT COUNT(*) as total_viajes FROM viaje;        -- 5
SELECT COUNT(*) as total_pagos FROM pago;          -- 6
```

---

## 3. Credenciales de Prueba

### Admin
- **Email:** `admin@mexicolindo.com`
- **Password:** `admin123`
- **Rol:** ADMIN

### Gestor
- **Email:** `gestor@mexicolindo.com`
- **Password:** `admin123`
- **Rol:** GESTOR

---

## 4. Datos Generados

### Camionetas (2 — las del schema)
| Nombre | Estado | KM Actual |
|--------|--------|-----------|
| Ximena | activa | 5,230 |
| Libertad | activa | 8,750 |

### Choferes (3 — los del schema)
| Nombre | Teléfono | Licencia Vence |
|--------|----------|---|
| Ángel | 5551234567 | 2027-03-31 |
| Piza | 5559876543 | 2026-08-15 (genera aviso ~30 días) |
| Peñalosa | 5555555555 | 2026-07-25 (genera aviso urgente) |

### Clientes (4)
| Nombre | Teléfono |
|--------|----------|
| Viajes Corporativos S.A. | 5551111111 |
| Turismo Beach Tours | 5552222222 |
| Agencia de Eventos MX | 5553333333 |
| Hotel La Hacienda | 5554444444 |

### Viajes (5)
| Cliente | Concepto | Camioneta | Estado | Costo |
|---------|----------|-----------|--------|-------|
| Viajes Corp | Viaje a Cancún | Ximena | finalizado | $3,500 |
| Viajes Corp | Playa del Carmen | Ximena | apartado | $3,000 |
| Turismo Beach | Tulum y Xel-Há | Libertad | finalizado | $4,000 |
| Agencia Eventos | Traslado boda | Libertad | en_curso | $2,500 |
| Hotel Hacienda | Traslado huéspedes | Ximena | apartado | $1,500 |

### Trámites con avisos (fechas pensadas para hoy 2026-07-16)
| Camioneta | Tipo | Vence | Aviso esperado |
|-----------|------|-------|----------------|
| Libertad | placas | 2026-07-10 | VENCIDO (persistente) |
| Libertad | verificación | 2026-07-20 | 4 días — urgente |
| Ximena | seguro | 2026-07-30 | 14 días |

---

## 5. Guía de Testing Frontend

### 5.1 Login
```
1. Ir a http://localhost:5173/admin/login
2. Email: admin@mexicolindo.com
3. Password: admin123
4. Click "Entrar"
```

**Esperado:** Redirige a `/admin/dashboard`

---

### 5.2 Ver Viajes
```
GET http://localhost:8080/api/viajes
```

**Esperado:**
```json
[
  {
    "id": 1,
    "clienteNombre": "Viajes Corporativos S.A.",
    "camionetaNombre": "URVAN-001",
    "choferNombre": "Carlos López García",
    "concepto": "Viaje a Cancún",
    "estado": "finalizado",
    "costoTotal": 3500.00,
    "kmInicial": 5000,
    "kmFinal": 5230
  },
  ...
]
```

---

### 5.3 Ver Dashboard
```
GET http://localhost:8080/api/dashboard/mes?mes=7&anio=2026
```

**Esperado:**
```json
{
  "periodo": "2026-07",
  "ingresosTotal": 14000.00,
  "egresosTotal": 3720.00,
  "netoTotal": 10280.00,
  "camionetas": [
    {
      "camionetaId": 1,
      "camionetaNombre": "URVAN-001",
      "ingresos": 6500.00,
      "egresos": 1870.00,
      "neto": 4630.00,
      "viajesCompletados": 2
    },
    {
      "camionetaId": 2,
      "camionetaNombre": "URVAN-002",
      "ingresos": 4000.00,
      "egresos": 1850.00,
      "neto": 2150.00,
      "viajesCompletados": 1
    }
  ]
}
```

---

### 5.4 Ver Avisos
```
GET http://localhost:8080/api/avisos
```

**Esperado:** Avisos de trámites próximos a vencer + mantenimientos

---

### 5.5 Crear Viaje (POST)
```
POST http://localhost:8080/api/viajes
Authorization: Bearer {token}
Content-Type: application/json

{
  "clienteId": 1,
  "camionetaId": 1,
  "choferId": 1,
  "concepto": "Nuevo viaje de prueba",
  "fechaInicio": "2026-08-05",
  "fechaFin": "2026-08-07",
  "costoTotal": 2500.00,
  "notas": "Viaje de prueba"
}
```

**Esperado:** 201 Created con datos del viaje creado

---

### 5.6 Agregar Pago (POST)
```
POST http://localhost:8080/api/viajes/1/pagos
Authorization: Bearer {token}

{
  "tipo": "liquidacion",
  "fechaPago": "2026-07-12",
  "monto": 1750.00,
  "metodo": "efectivo",
  "notas": "Pago final"
}
```

---

### 5.7 Finalizar Viaje (PUT)
```
PUT http://localhost:8080/api/viajes/2/finalizar
Authorization: Bearer {token}

{
  "kmFinal": 5450
}
```

---

## 6. Checklist de Pruebas

### Backend
- [ ] Login retorna token JWT válido
- [ ] GET /viajes retorna 5 registros
- [ ] GET /viajes/{id} retorna viaje específico
- [ ] POST /viajes crea nuevo viaje
- [ ] PUT /viajes/{id} actualiza viaje
- [ ] GET /dashboard/mes calcula ingresos/egresos
- [ ] GET /avisos retorna avisos de vencimientos
- [ ] GET /calendario retorna ocupación por fecha
- [ ] DELETE /viajes/{id}/cancelar marca como cancelado

### Frontend
- [ ] Login funciona con credenciales
- [ ] Dashboard carga y muestra ingresos/egresos
- [ ] Tabla de viajes se rellena
- [ ] Crear viaje abre formulario
- [ ] Guardar viaje hace POST exitoso
- [ ] Editar viaje hace PUT exitoso
- [ ] Cancelar viaje hace DELETE exitoso
- [ ] Agregar pago actualiza tabla
- [ ] CORS funciona (sin errores de origen)

---

## 7. Troubleshooting

### Error: "Credenciales inválidas"
- Verifica que usuarios están en BD con `SELECT * FROM usuario;`
- Revisa que BCrypt hash sea correcto
- Prueba con `mysql -u root -p` directamente

### Error: "CORS blocked"
- Confirma que `WebConfig.java` está configurado
- Reinicia backend después de agregar config
- Revisa headers de respuesta con DevTools

### Error: 404 en endpoint
- Verifica ruta en controller (ej: `/viajes` no `/viajes/`)
- Confirma que endpoint existe en código
- Revisa que no hay typos en nombres

### Error: "Viaje no encontrado"
- Confirma ID del viaje en BD
- Verifica que estado permite la operación (ej: no finalizar un cancelado)

---

## 8. Postman Collection

Si usas Postman, importa esta colección básica:

```json
{
  "info": {
    "name": "Mexico Lindo Tours",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/auth/login",
            "body": {
              "mode": "raw",
              "raw": "{\"correo\":\"admin@mexicolindo.com\",\"password\":\"admin123\"}"
            }
          }
        }
      ]
    },
    {
      "name": "Viajes",
      "item": [
        {
          "name": "Get all",
          "request": {
            "method": "GET",
            "url": "{{base_url}}/viajes",
            "header": [
              {"key": "Authorization", "value": "Bearer {{token}}"}
            ]
          }
        },
        {
          "name": "Create",
          "request": {
            "method": "POST",
            "url": "{{base_url}}/viajes",
            "body": {
              "mode": "raw",
              "raw": "{\"clienteId\":1,\"camionetaId\":1,\"choferId\":1,\"concepto\":\"Test\",\"fechaInicio\":\"2026-08-10\",\"fechaFin\":\"2026-08-12\",\"costoTotal\":2000}"
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {"key": "base_url", "value": "http://localhost:8080/api"},
    {"key": "token", "value": ""}
  ]
}
```

---

## 9. Logs Importantes

### Backend
```bash
# Ver logs de Spring Boot
tail -f nohup.out

# O en consola si ejecutas: java -jar app.jar
```

### Frontend (DevTools)
```javascript
// Abre consola F12
// Verifica requests en Network tab
// Busca errores CORS o 401
```

---

## 10. Reset Total

Si necesitas empezar de cero:

```sql
-- Opción 1: Truncar tablas (mantiene estructura)
TRUNCATE TABLE gasto;
TRUNCATE TABLE pago;
TRUNCATE TABLE viaje;
TRUNCATE TABLE disponibilidad_chofer;
TRUNCATE TABLE gasto_general;
TRUNCATE TABLE tramite_vehiculo;
TRUNCATE TABLE mantenimiento;
TRUNCATE TABLE chofer;
TRUNCATE TABLE cliente;
TRUNCATE TABLE camioneta;
TRUNCATE TABLE usuario;

-- Opción 2: Dropear y recrear BD
DROP DATABASE mexico_lindo;
CREATE DATABASE mexico_lindo;
-- Luego ejecuta schema.sql
```

Luego recarga `data-prueba.sql`.

---

**¡Listo para testear!** 🚀
