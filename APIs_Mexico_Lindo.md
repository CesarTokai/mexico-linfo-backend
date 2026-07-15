# APIs Mexico Lindo Tours — Especificación Completa

## AUTH

### POST /auth/login
**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "contraseña"
}
```
**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {P
    "id": 1,
    "email": "usuario@example.com",
    "nombre": "Juan",
    "rol": "ADMIN"
  }
}
```
**Error (401):**
```json
{
  "error": "Credenciales inválidas"
}
```

---

## USUARIOS (CRUD)

### GET /usuarios
**Query Params:** ninguno
**Response (200):**
```json
{
  "usuarios": [
    {
      "id": 1,
      "email": "admin@example.com",
      "nombre": "Admin User",
      "rol": "ADMIN",
      "activo": true,
      "creado_en": "2026-01-15T10:30:00Z"
    },
    {
      "id": 2,
      "email": "gestor@example.com",
      "nombre": "Gestor User",
      "rol": "GESTOR",
      "activo": true,
      "creado_en": "2026-02-10T14:20:00Z"
    }
  ]
}
```

### POST /usuarios
**Request:**
```json
{
  "email": "nuevo@example.com",
  "nombre": "Nuevo Usuario",
  "password": "contraseña123",
  "rol": "GESTOR"
}
```
**Response (201):**
```json
{
  "id": 3,
  "email": "nuevo@example.com",
  "nombre": "Nuevo Usuario",
  "rol": "GESTOR",
  "activo": true,
  "creado_en": "2026-07-15T12:00:00Z"
}
```

### PUT /usuarios/{id}
**Request:**
```json
{
  "nombre": "Nuevo Nombre",
  "rol": "ADMIN"
}
```
**Response (200):**
```json
{
  "id": 3,
  "email": "nuevo@example.com",
  "nombre": "Nuevo Nombre",
  "rol": "ADMIN",
  "activo": true
}
```

### DELETE /usuarios/{id}
**Response (200):**
```json
{
  "mensaje": "Usuario desactivado"
}
```

---

## CAMIONETAS

### GET /camionetas
**Response (200):**
```json
{
  "camionetas": [
    {
      "id": 1,
      "placa": "MLT-001",
      "modelo": "Urvan NV350",
      "anio": 2023,
      "km_actual": 45230,
      "km_mantenimiento": 10000,
      "proximo_mantenimiento_km": 50230,
      "estado": "disponible",
      "color": "blanco",
      "creada_en": "2026-01-10T08:00:00Z"
    }
  ]
}
```

### POST /camionetas
**Request:**
```json
{
  "placa": "MLT-002",
  "modelo": "Urvan NV350",
  "anio": 2024,
  "km_actual": 5000,
  "km_mantenimiento": 10000,
  "color": "blanco"
}
```
**Response (201):**
```json
{
  "id": 2,
  "placa": "MLT-002",
  "modelo": "Urvan NV350",
  "anio": 2024,
  "km_actual": 5000,
  "km_mantenimiento": 10000,
  "estado": "disponible"
}
```

### PUT /camionetas/{id}
**Request:**
```json
{
  "km_actual": 50000,
  "estado": "en_taller",
  "km_mantenimiento": 15000
}
```
**Response (200):**
```json
{
  "id": 1,
  "placa": "MLT-001",
  "km_actual": 50000,
  "estado": "en_taller",
  "km_mantenimiento": 15000
}
```

### DELETE /camionetas/{id}
**Response (200):**
```json
{
  "mensaje": "Camioneta desactivada"
}
```

### GET /camionetas/{id}/historial
**Response (200):**
```json
{
  "camioneta_id": 1,
  "placa": "MLT-001",
  "historial": [
    {
      "viaje_id": 10,
      "fecha_inicio": "2026-07-10",
      "fecha_fin": "2026-07-12",
      "cliente": "Cliente A",
      "km_inicial": 45000,
      "km_final": 45230,
      "ingresos": 2500.00,
      "egresos": 350.00,
      "neto": 2150.00
    }
  ]
}
```

---

## CHOFERES

### GET /choferes
**Response (200):**
```json
{
  "choferes": [
    {
      "id": 1,
      "nombre": "Juan García",
      "telefono": "+52 555-1234",
      "licencia_numero": "LIC123456",
      "licencia_vencimiento": "2027-06-15",
      "estado": "activo",
      "creado_en": "2026-01-01T08:00:00Z"
    }
  ]
}
```

### POST /choferes
**Request:**
```json
{
  "nombre": "Pedro López",
  "telefono": "+52 555-5678",
  "licencia_numero": "LIC789012",
  "licencia_vencimiento": "2027-12-31"
}
```
**Response (201):**
```json
{
  "id": 2,
  "nombre": "Pedro López",
  "telefono": "+52 555-5678",
  "licencia_numero": "LIC789012",
  "licencia_vencimiento": "2027-12-31",
  "estado": "activo"
}
```

### PUT /choferes/{id}
**Request:**
```json
{
  "nombre": "Pedro López Actualizado",
  "licencia_vencimiento": "2028-12-31"
}
```
**Response (200):**
```json
{
  "id": 2,
  "nombre": "Pedro López Actualizado",
  "licencia_vencimiento": "2028-12-31"
}
```

### DELETE /choferes/{id}
**Response (200):**
```json
{
  "mensaje": "Chofer desactivado"
}
```

### GET /choferes/{id}/historial
**Response (200):**
```json
{
  "chofer_id": 1,
  "nombre": "Juan García",
  "total_viajes": 45,
  "km_totales": 12500,
  "historial": [
    {
      "viaje_id": 10,
      "fecha": "2026-07-10",
      "camioneta": "MLT-001",
      "cliente": "Cliente A",
      "km_recorridos": 230,
      "pago": 500.00
    }
  ]
}
```

### GET /choferes/{id}/disponibilidad
**Response (200):**
```json
{
  "chofer_id": 1,
  "disponibilidades": [
    {
      "id": 1,
      "fecha": "2026-07-20",
      "disponible": true,
      "notas": "Disponible"
    }
  ]
}
```

### POST /choferes/{id}/disponibilidad
**Request:**
```json
{
  "fecha": "2026-07-20",
  "disponible": true,
  "notas": "Disponible todo el día"
}
```
**Response (201):**
```json
{
  "id": 1,
  "chofer_id": 1,
  "fecha": "2026-07-20",
  "disponible": true,
  "notas": "Disponible todo el día"
}
```

---

## CLIENTES

### GET /clientes
**Response (200):**
```json
{
  "clientes": [
    {
      "id": 1,
      "nombre": "Cliente A",
      "telefono": "+52 555-1111",
      "email": "cliente@example.com",
      "total_viajes": 15,
      "estado": "activo",
      "creado_en": "2026-01-05T10:00:00Z"
    }
  ]
}
```

### POST /clientes
**Request:**
```json
{
  "nombre": "Cliente Nuevo",
  "telefono": "+52 555-2222",
  "email": "nuevo@example.com"
}
```
**Response (201):**
```json
{
  "id": 2,
  "nombre": "Cliente Nuevo",
  "telefono": "+52 555-2222",
  "email": "nuevo@example.com",
  "total_viajes": 0,
  "estado": "activo"
}
```

### PUT /clientes/{id}
**Request:**
```json
{
  "nombre": "Cliente Nuevo Actualizado",
  "email": "actualizado@example.com"
}
```
**Response (200):**
```json
{
  "id": 2,
  "nombre": "Cliente Nuevo Actualizado",
  "email": "actualizado@example.com"
}
```

### DELETE /clientes/{id}
**Response (200):**
```json
{
  "mensaje": "Cliente desactivado"
}
```

### GET /clientes/{id}/historial
**Response (200):**
```json
{
  "cliente_id": 1,
  "nombre": "Cliente A",
  "total_viajes": 15,
  "ingresos_totales": 37500.00,
  "historial": [
    {
      "viaje_id": 10,
      "fecha_inicio": "2026-07-10",
      "fecha_fin": "2026-07-12",
      "camioneta": "MLT-001",
      "costo_total": 2500.00,
      "pagado": 2500.00,
      "estado": "finalizado"
    }
  ]
}
```

---

## VIAJES (CRUD Complejo)

### GET /viajes
**Query Params:** `?estado=en_curso&mes=7&anio=2026`
**Response (200):**
```json
{
  "viajes": [
    {
      "id": 10,
      "cliente_id": 1,
      "cliente_nombre": "Cliente A",
      "camioneta_id": 1,
      "camioneta_placa": "MLT-001",
      "chofer_id": 1,
      "chofer_nombre": "Juan García",
      "fecha_inicio": "2026-07-10",
      "fecha_fin": "2026-07-12",
      "km_inicial": 45000,
      "km_final": 45230,
      "costo_total": 2500.00,
      "estado": "finalizado",
      "notas": "Cliente satisfecho",
      "creado_en": "2026-07-09T14:30:00Z"
    }
  ]
}
```

### POST /viajes
**Request:**
```json
{
  "cliente_id": 1,
  "camioneta_id": 1,
  "chofer_id": 1,
  "fecha_inicio": "2026-07-20",
  "fecha_fin": "2026-07-22",
  "km_inicial": 50230,
  "costo_total": 3000.00,
  "notas": "Renta con conductor"
}
```
**Response (201):**
```json
{
  "id": 11,
  "cliente_id": 1,
  "camioneta_id": 1,
  "chofer_id": 1,
  "fecha_inicio": "2026-07-20",
  "fecha_fin": "2026-07-22",
  "km_inicial": 50230,
  "costo_total": 3000.00,
  "estado": "apartado",
  "pagos": [],
  "gastos": [],
  "creado_en": "2026-07-15T12:00:00Z"
}
```

### PUT /viajes/{id}
**Request:**
```json
{
  "chofer_id": 2,
  "fecha_inicio": "2026-07-21",
  "fecha_fin": "2026-07-23",
  "costo_total": 3200.00
}
```
**Response (200):**
```json
{
  "id": 11,
  "cliente_id": 1,
  "chofer_id": 2,
  "fecha_inicio": "2026-07-21",
  "fecha_fin": "2026-07-23",
  "costo_total": 3200.00,
  "estado": "apartado"
}
```

### DELETE /viajes/{id}
**Response (200):**
```json
{
  "mensaje": "Viaje cancelado"
}
```

### PUT /viajes/{id}/estado
**Request:**
```json
{
  "estado": "en_curso"
}
```
**Response (200):**
```json
{
  "id": 11,
  "estado": "en_curso",
  "mensaje": "Viaje iniciado"
}
```

**Estados válidos:** `apartado` → `en_curso` → `finalizado` | `cancelado`

### GET /viajes/{id}/pagos
**Response (200):**
```json
{
  "viaje_id": 10,
  "pagos": [
    {
      "id": 1,
      "tipo": "apartado",
      "monto": 1000.00,
      "fecha_pago": "2026-07-09",
      "metodo": "transferencia",
      "notas": "Primer adelanto"
    },
    {
      "id": 2,
      "tipo": "liquidacion",
      "monto": 1500.00,
      "fecha_pago": "2026-07-12",
      "metodo": "efectivo",
      "notas": "Pago final"
    }
  ],
  "pagado_total": 2500.00,
  "pendiente": 0.00
}
```

### POST /viajes/{id}/pagos
**Request:**
```json
{
  "tipo": "abono",
  "monto": 500.00,
  "fecha_pago": "2026-07-15",
  "metodo": "efectivo",
  "notas": "Abono extra"
}
```
**Response (201):**
```json
{
  "id": 3,
  "viaje_id": 10,
  "tipo": "abono",
  "monto": 500.00,
  "fecha_pago": "2026-07-15",
  "metodo": "efectivo"
}
```

### PUT /viajes/{id}/pagos/{pago_id}
**Request:**
```json
{
  "monto": 600.00,
  "metodo": "transferencia"
}
```
**Response (200):**
```json
{
  "id": 3,
  "viaje_id": 10,
  "monto": 600.00,
  "metodo": "transferencia"
}
```

### GET /viajes/{id}/gastos
**Response (200):**
```json
{
  "viaje_id": 10,
  "gastos": [
    {
      "id": 1,
      "tipo": "chofer",
      "monto": 300.00,
      "fecha": "2026-07-12",
      "notas": "Pago chofer Juan García"
    },
    {
      "id": 2,
      "tipo": "gasolina",
      "monto": 50.00,
      "fecha": "2026-07-12",
      "notas": "Combustible"
    }
  ],
  "total_egresos": 350.00
}
```

### POST /viajes/{id}/gastos
**Request:**
```json
{
  "tipo": "caseta",
  "monto": 100.00,
  "fecha": "2026-07-10",
  "notas": "Caseta Mexico City"
}
```
**Response (201):**
```json
{
  "id": 3,
  "viaje_id": 10,
  "tipo": "caseta",
  "monto": 100.00,
  "fecha": "2026-07-10"
}
```

### PUT /viajes/{id}/gastos/{gasto_id}
**Request:**
```json
{
  "monto": 120.00
}
```
**Response (200):**
```json
{
  "id": 3,
  "viaje_id": 10,
  "monto": 120.00
}
```

---

## MANTENIMIENTOS

### GET /mantenimientos
**Response (200):**
```json
{
  "mantenimientos": [
    {
      "id": 1,
      "camioneta_id": 1,
      "camioneta_placa": "MLT-001",
      "fecha": "2026-07-10",
      "km_realizado": 50000,
      "tipo": "cambio_aceite",
      "costo": 500.00,
      "notas": "Mantenimiento preventivo",
      "prox_km": 60000
    }
  ]
}
```

### POST /mantenimientos
**Request:**
```json
{
  "camioneta_id": 1,
  "fecha": "2026-07-15",
  "km_realizado": 50230,
  "tipo": "revision_general",
  "costo": 1200.00,
  "notas": "Revisión general completa"
}
```
**Response (201):**
```json
{
  "id": 2,
  "camioneta_id": 1,
  "fecha": "2026-07-15",
  "km_realizado": 50230,
  "tipo": "revision_general",
  "costo": 1200.00,
  "prox_km": 60230
}
```

### PUT /mantenimientos/{id}
**Request:**
```json
{
  "costo": 1300.00,
  "notas": "Costo revisado"
}
```
**Response (200):**
```json
{
  "id": 2,
  "camioneta_id": 1,
  "costo": 1300.00
}
```

---

## TRÁMITES

### GET /tramites
**Response (200):**
```json
{
  "tramites": [
    {
      "id": 1,
      "camioneta_id": 1,
      "camioneta_placa": "MLT-001",
      "tipo": "tenencia",
      "fecha_vencimiento": "2027-06-15",
      "dias_para_vencer": 336,
      "estado": "vigente",
      "costo": 800.00,
      "notas": "Tenencia 2027"
    }
  ]
}
```

### POST /tramites
**Request:**
```json
{
  "camioneta_id": 1,
  "tipo": "seguro",
  "fecha_vencimiento": "2027-07-15",
  "costo": 3500.00,
  "notas": "Seguro de responsabilidad civil"
}
```
**Response (201):**
```json
{
  "id": 2,
  "camioneta_id": 1,
  "tipo": "seguro",
  "fecha_vencimiento": "2027-07-15",
  "costo": 3500.00
}
```

### PUT /tramites/{id}
**Request:**
```json
{
  "fecha_vencimiento": "2028-07-15",
  "costo": 3600.00
}
```
**Response (200):**
```json
{
  "id": 2,
  "camioneta_id": 1,
  "fecha_vencimiento": "2028-07-15",
  "costo": 3600.00
}
```

---

## GASTOS GENERALES

### GET /gastos-generales
**Query Params:** `?mes=7&anio=2026`
**Response (200):**
```json
{
  "gastos": [
    {
      "id": 1,
      "categoria": "oficina",
      "descripcion": "Renta oficina",
      "monto": 5000.00,
      "fecha": "2026-07-01",
      "creado_en": "2026-06-25T08:00:00Z"
    }
  ],
  "total_mes": 5000.00
}
```

### POST /gastos-generales
**Request:**
```json
{
  "categoria": "seguros",
  "descripcion": "Seguro responsabilidad civil empresa",
  "monto": 2000.00,
  "fecha": "2026-07-15"
}
```
**Response (201):**
```json
{
  "id": 2,
  "categoria": "seguros",
  "descripcion": "Seguro responsabilidad civil empresa",
  "monto": 2000.00,
  "fecha": "2026-07-15"
}
```

### PUT /gastos-generales/{id}
**Request:**
```json
{
  "monto": 2100.00,
  "descripcion": "Seguro actualizado"
}
```
**Response (200):**
```json
{
  "id": 2,
  "monto": 2100.00,
  "descripcion": "Seguro actualizado"
}
```

---

## CALENDARIO

### GET /calendario
**Query Params:** `?desde=2026-07-01&hasta=2026-07-31`
**Response (200):**
```json
{
  "ocupacion": [
    {
      "camioneta_id": 1,
      "placa": "MLT-001",
      "estado": "disponible",
      "viajes": [
        {
          "viaje_id": 10,
          "cliente": "Cliente A",
          "fecha_inicio": "2026-07-10",
          "fecha_fin": "2026-07-12",
          "estado": "finalizado"
        },
        {
          "viaje_id": 11,
          "cliente": "Cliente B",
          "fecha_inicio": "2026-07-20",
          "fecha_fin": "2026-07-23",
          "estado": "apartado"
        }
      ]
    },
    {
      "camioneta_id": 2,
      "placa": "MLT-002",
      "estado": "en_taller",
      "mantenimiento": {
        "fecha": "2026-07-15",
        "tipo": "revision_general"
      }
    }
  ]
}
```

---

## TOTALES (Contabilidad)

### GET /totales
**Query Params:** `?mes=7&anio=2026` (opcional, sin parámetros = acumulado)
**Response (200):**
```json
{
  "periodo": "Julio 2026",
  "ingresos": {
    "total_viajes": 25000.00,
    "descripcion": "Suma de costo_total de viajes completados"
  },
  "egresos": {
    "gastos_viajes": 3500.00,
    "gastos_camionetas": 2000.00,
    "gastos_generales": 5000.00,
    "total": 10500.00
  },
  "neto": 14500.00,
  "detalle_camionetas": [
    {
      "camioneta_id": 1,
      "placa": "MLT-001",
      "ingresos": 15000.00,
      "egresos": 5000.00,
      "neto": 10000.00
    }
  ]
}
```

---

## AVISOS

### GET /avisos
**Response (200):**
```json
{
  "avisos": {
    "mantenimientos_proximamente": [
      {
        "camioneta_id": 1,
        "placa": "MLT-001",
        "km_actual": 50000,
        "km_proximo": 60000,
        "km_faltantes": 10000,
        "alertas": [
          {
            "nivel": "alerta",
            "mensaje": "Faltando 500 km para mantenimiento",
            "km_alerta": 59500
          }
        ]
      }
    ],
    "tramites_por_vencer": [
      {
        "tramite_id": 1,
        "camioneta_placa": "MLT-001",
        "tipo": "tenencia",
        "vencimiento": "2027-06-15",
        "dias_restantes": 336,
        "alertas": [
          {
            "nivel": "info",
            "mensaje": "Vence en 30 días",
            "dias": 30
          }
        ]
      }
    ],
    "licencias_chofer_por_vencer": [
      {
        "chofer_id": 1,
        "nombre": "Juan García",
        "licencia_vencimiento": "2027-06-15",
        "dias_restantes": 336,
        "alertas": []
      }
    ]
  }
}
```

---

## DASHBOARD (Ganancia por Camioneta)

### GET /dashboard
**Query Params:** `?mes=7&anio=2026` (opcional, sin parámetros = acumulado)
**Response (200):**
```json
{
  "periodo": "Julio 2026",
  "resumen_general": {
    "ingresos_total": 25000.00,
    "egresos_total": 10500.00,
    "neto_total": 14500.00
  },
  "ganancia_por_camioneta": [
    {
      "camioneta_id": 1,
      "placa": "MLT-001",
      "ingresos": 15000.00,
      "egresos": {
        "chofer": 2000.00,
        "gasolina": 1500.00,
        "casetas": 300.00,
        "mantenimiento": 500.00,
        "otros": 200.00,
        "total": 4500.00
      },
      "neto": 10500.00,
      "porcentaje_util": 70.0,
      "viajes_completados": 6
    },
    {
      "camioneta_id": 2,
      "placa": "MLT-002",
      "ingresos": 10000.00,
      "egresos": 4000.00,
      "neto": 6000.00,
      "porcentaje_util": 40.0,
      "viajes_completados": 3
    }
  ]
}
```

---

## NOTAS TÉCNICAS

- **Dinero:** siempre `DECIMAL` (BigDecimal en Java)
- **Fechas:** ISO 8601 (`YYYY-MM-DD`, timestamps con Z)
- **Autenticación:** token JWT en header `Authorization: Bearer <token>`
- **Roles:** ADMIN (todo), GESTOR (operación sin usuarios/eliminar viajes finalizados)
- **Ciclos de vida:**
  - Viaje: `apartado` → `en_curso` → `finalizado` o `cancelado`
  - Camioneta: `disponible` o `en_taller`
  - Datos: baja lógica (nunca DELETE físico)
- **Validaciones:**
  - Anti-doble-reserva: no traslapes fecha/camioneta (excepción: mismo día fin-inicio)
  - KM: `km_final > km_inicial` y `km_inicial >= camioneta.km_actual`
  - Trámites/Licencias: avisos 30/15/10/5 días antes; persisten si vencen
