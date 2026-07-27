# APIs México Lindo Tours — Referencia Completa

> Generada desde el código fuente (controllers/DTOs) — refleja el comportamiento real del backend.

## Configuración base

- **Base URL:** `http://localhost:8080` (SIN prefijo `/api` — no hay context-path configurado)
- **CORS permitido:** `http://localhost:5173` y `http://localhost:3000`
- **Auth:** JWT Bearer. Header `Authorization: Bearer <token>` en TODO excepto `POST /auth/login`.
- **Token:** expira en 24 h (`jwt.expiration=86400000` ms).
- **Fechas:** formato `YYYY-MM-DD` (LocalDate). Dinero: número decimal (BigDecimal).

### Formato de errores — IMPORTANTE

Los errores devuelven **texto plano**, NO JSON:

```
HTTP 400
Body: "Camioneta no encontrada"
```

En frontend usar `res.text()` para errores, no `res.json()` (fallaría el parse).

### Roles

| Regla | Quién |
|---|---|
| Todo `/usuarios/**` (GET/POST/PUT/DELETE) | Solo **ADMIN** (403 si GESTOR) |
| `POST /auth/crear-usuario` | Solo **ADMIN** |
| `DELETE /viajes/{id}/cancelar` | Solo **ADMIN** |
| `PUT /viajes/{id}/estado` con `"cancelado"` | Solo **ADMIN** (403 con mensaje) |
| `PUT /viajes/{id}` sobre viaje `finalizado` | Solo **ADMIN** (GESTOR recibe 400) |
| Todo lo demás | Cualquier autenticado (ADMIN o GESTOR) |

### Enums (valores exactos)

| Enum | Valores |
|---|---|
| Viaje.estado | `apartado`, `en_curso`, `finalizado`, `cancelado` |
| Camioneta.estado | `activa`, `en_taller`, `baja` |
| Pago.tipo | `apartado`, `liquidacion`, `abono` |
| Gasto.tipo | `caseta`, `gasolina`, `chofer`, `otros` |
| Mantenimiento.tipo | `mantenimiento`, `refaccion` |
| Tramite.tipo | `tenencia`, `placas`, `seguro`, `verificacion`, `otro` |
| Usuario.rol | `ADMIN`, `GESTOR` |

---

## AUTH

### POST /auth/login  (público)
```json
// Request
{ "correo": "admin@mlt.com", "password": "secreto123" }

// Response 200
{ "token": "eyJ...", "nombre": "Admin", "correo": "admin@mlt.com", "rol": "ADMIN" }
```
Error 401: texto `"Credenciales inválidas"`.

### POST /auth/crear-usuario  (ADMIN)
⚠️ Usa **query params**, no JSON body (endpoint legado; preferir `POST /usuarios`):
```
POST /auth/crear-usuario?nombre=Juan&correo=j@mlt.com&password=abc123&rol=GESTOR
```
Response 201: texto `"Usuario creado: Juan"`.

---

## USUARIOS  (todo ADMIN-only)

### GET /usuarios
```json
// Response 200 — array plano
[ { "id": 1, "nombre": "Admin", "correo": "admin@mlt.com", "rol": "ADMIN", "activo": true } ]
```

### GET /usuarios/{id}
Response 200: objeto UsuarioDTO (igual que arriba). 404: texto.

### POST /usuarios
```json
// Request  (rol opcional, default GESTOR)
{ "nombre": "Juan", "email": "j@mlt.com", "password": "abc123", "rol": "GESTOR" }
```
⚠️ El campo del request se llama `email` (el response lo devuelve como `correo`).
Response 201: UsuarioDTO.

### PUT /usuarios/{id}
```json
{ "nombre": "Juan Nuevo", "rol": "ADMIN" }
```
Campos opcionales (null = no cambia). Response 200: UsuarioDTO.

### DELETE /usuarios/{id}
Baja lógica. Response 200: texto `"Usuario desactivado"`.

---

## CAMIONETAS

### GET /camionetas
```json
[ { "id": 1, "nombre": "Urvan 1", "modelo": "NV350 2022", "capacidad": 14,
    "kmActual": 45200, "intervaloMantenimientoKm": 10000, "estado": "activa" } ]
```

### GET /camionetas/{id} → CamionetaDTO | 404 texto

### POST /camionetas
```json
{ "nombre": "Urvan 3", "modelo": "NV350 2023", "capacidad": 14 }
```
Solo se usan `nombre`, `modelo`, `capacidad` (otros campos del DTO se ignoran).
Response 201: CamionetaDTO.

### PUT /camionetas/{id}
```json
{ "nombre": "Urvan 3", "modelo": "NV350 2023", "capacidad": 14,
  "estado": "en_taller", "kmMantenimiento": 10000 }
```
Todos opcionales. `estado`: `activa` | `en_taller` | `baja`. `kmMantenimiento` = intervalo de mantenimiento.
Response 200: CamionetaDTO.

### DELETE /camionetas/{id}
Baja lógica. Response 200: texto `"Camioneta desactivada"`.

### GET /camionetas/{id}/historial
```json
{ "camionetaId": 1, "camionetaNombre": "Urvan 1", "totalViajes": 32,
  "kmActual": 45200, "costosMantenimiento": 15400.00, "costosTramites": 8900.00 }
```

---

## CHOFERES

### GET /choferes — solo activos
### GET /choferes/todos — incluye inactivos
```json
[ { "id": 1, "nombre": "Pedro", "telefono": "7771234567",
    "licenciaVencimiento": "2026-12-01", "activo": true } ]
```

### GET /choferes/{id} → ChoferDTO | 404 texto

### POST /choferes
```json
{ "nombre": "Pedro", "telefono": "7771234567" }
```
Solo se usan `nombre` y `telefono`. Response 201: ChoferDTO.

### PUT /choferes/{id}
```json
{ "nombre": "Pedro", "telefono": "7771234567", "licenciaVencimiento": "2027-06-15" }
```
Todos opcionales. Response 200: ChoferDTO.

### DELETE /choferes/{id}
Baja lógica. Response 200: texto `"Chofer desactivado"`.

### GET /choferes/{id}/historial
```json
{ "choferId": 1, "choferNombre": "Pedro", "totalViajes": 18,
  "kmManejados": 12400, "totalPagado": 27000.00 }
```

### Disponibilidad de chofer

**GET /choferes/{id}/disponibilidad**
```json
[ { "id": 5, "choferId": 1, "fecha": "2026-08-01", "disponible": true, "notas": null } ]
```

**POST /choferes/{id}/disponibilidad** (crea o actualiza si ya existe esa fecha)
```json
{ "fecha": "2026-08-01", "disponible": false, "notas": "Viaje personal" }
```
Response 201: DisponibilidadChoferDTO.

**DELETE /choferes/disponibilidad/{disponibilidadId}**
Response 200: texto `"Disponibilidad eliminada"`.

---

## CLIENTES

### GET /clientes
```json
[ { "id": 1, "nombre": "María López", "telefono": "5551112233", "notas": null } ]
```

### GET /clientes/{id} → ClienteDTO | 404 texto

### POST /clientes
```json
{ "nombre": "María López", "telefono": "5551112233", "email": "maria@mail.com" }
```
Response 201: ClienteDTO.

### PUT /clientes/{id}
```json
{ "nombre": "María L.", "telefono": "5551112233", "email": "maria@mail.com" }
```
Response 200: ClienteDTO. (No hay DELETE de clientes.)

### GET /clientes/{id}/historial
```json
{ "clienteId": 1, "clienteNombre": "María López", "totalViajes": 5,
  "totalPagado": 42000.00, "pendiente": 3000.00 }
```

---

## VIAJES

### ViajeDTO (respuesta de todos los endpoints de viaje)
```json
{ "id": 10, "clienteId": 1, "clienteNombre": "María López",
  "camionetaId": 1, "camionetaNombre": "Urvan 1",
  "choferId": 2, "choferNombre": "Pedro",
  "concepto": "Viaje a Acapulco", "fechaInicio": "2026-08-10", "fechaFin": "2026-08-12",
  "kmInicial": 45200, "kmFinal": null, "costoTotal": 12000.00,
  "estado": "apartado", "notas": null, "pagos": null, "gastos": null }
```
`choferId`/`choferNombre` son `null` si no hay chofer asignado. `pagos`/`gastos` siempre `null` aquí — usar los endpoints anidados.

### GET /viajes — todos (sin query params)
### GET /viajes/{id} → ViajeDTO | 404
### GET /viajes/camioneta/{camionetaId} — viajes de una unidad
### GET /viajes/cliente/{clienteId} — viajes de un cliente

### POST /viajes
```json
{ "clienteId": 1, "camionetaId": 1, "choferId": null,
  "concepto": "Viaje a Acapulco", "fechaInicio": "2026-08-10",
  "fechaFin": "2026-08-12", "costoTotal": 12000.00, "notas": "opcional" }
```
`choferId` opcional (reasignable después). Valida anti-doble-reserva (400 si hay traslape con viaje no cancelado de la misma camioneta; se permite que un viaje termine el día que otro inicia). Camioneta `en_taller` o `baja` → 400.
Response 201: ViajeDTO (estado `apartado`).

### PUT /viajes/{id}
```json
{ "concepto": "...", "fechaInicio": "2026-08-11", "fechaFin": "2026-08-13",
  "costoTotal": 13000.00, "kmInicial": 45200, "choferId": 3, "notas": "..." }
```
Todos opcionales. Revalida anti-doble-reserva si cambian fechas.
⚠️ Viaje `finalizado`: solo ADMIN puede editarlo (GESTOR → 400 texto).

### PUT /viajes/{id}/finalizar
```json
{ "kmFinal": 45900 }
```
Valida `kmFinal > kmInicial`. Actualiza `camioneta.kmActual`. Response 200: ViajeDTO.

### PUT /viajes/{id}/estado
```json
{ "estado": "en_curso" }
```
Valores: `apartado` | `en_curso` | `finalizado` | `cancelado`.
⚠️ `cancelado` requiere ADMIN → GESTOR recibe 403 texto `"Solo ADMIN puede cancelar viajes"`.

### DELETE /viajes/{id}/cancelar  (ADMIN)
Marca `cancelado` (no borra; pagos recibidos cuentan como ingreso, adelanto no se devuelve).
Response 200: texto `"Viaje cancelado"`.

### Pagos del viaje

**GET /viajes/{id}/pagos**
```json
[ { "id": 1, "viajeId": 10, "tipo": "apartado", "fecha": "2026-07-20",
    "monto": 4000.00, "notas": null } ]
```

**POST /viajes/{id}/pagos**
```json
{ "tipo": "apartado", "fechaPago": "2026-07-20", "monto": 4000.00,
  "metodo": "efectivo", "notas": "opcional" }
```
`tipo`: `apartado` | `liquidacion` | `abono`. Response 201: PagoDTO.

**DELETE /viajes/pagos/{pagoId}** → 200 texto `"Pago eliminado"`.
(No existe PUT de pagos — pa corregir: eliminar y volver a crear.)

### Gastos del viaje

**GET /viajes/{id}/gastos**
```json
[ { "id": 1, "viajeId": 10, "tipo": "gasolina", "descripcion": null, "monto": 1500.00 } ]
```

**POST /viajes/{id}/gastos**
```json
{ "tipo": "gasolina", "fecha": "2026-08-10", "monto": 1500.00, "notas": "opcional" }
```
`tipo`: `caseta` | `gasolina` | `chofer` | `otros`. Response 201: GastoDTO.

**DELETE /viajes/gastos/{gastoId}** → 200 texto `"Gasto eliminado"`.
(No existe PUT de gastos.)

---

## MANTENIMIENTOS

### GET /mantenimientos | GET /mantenimientos/{id} | GET /mantenimientos/camioneta/{camionetaId}
```json
[ { "id": 1, "camionetaId": 1, "camionetaNombre": "Urvan 1", "fecha": "2026-07-01",
    "kmAlMomento": 40000, "tipo": "mantenimiento", "descripcion": "Cambio de aceite",
    "costo": 2500.00 } ]
```

### POST /mantenimientos
```json
{ "camionetaId": 1, "fecha": "2026-07-01", "tipo": "mantenimiento",
  "costo": 2500.00, "descripcion": "Cambio de aceite" }
```
`tipo`: `mantenimiento` | `refaccion`. `kmAlMomento` se toma automático del km actual de la unidad. Registrar mantenimiento resetea el aviso por km.
Response 201: MantenimientoDTO.

### PUT /mantenimientos/{id}
```json
{ "fecha": "2026-07-02", "costo": 2600.00, "descripcion": "..." }
```
Response 200: MantenimientoDTO.

### DELETE /mantenimientos/{id} → 200 texto.

---

## TRÁMITES

### GET /tramites | GET /tramites/{id} | GET /tramites/camioneta/{camionetaId}
```json
[ { "id": 1, "camionetaId": 1, "camionetaNombre": "Urvan 1", "tipo": "seguro",
    "fechaPago": "2026-01-15", "monto": 18000.00,
    "fechaVencimiento": "2027-01-15", "notas": null } ]
```

### POST /tramites
```json
{ "camionetaId": 1, "tipo": "seguro", "fechaPago": "2026-01-15",
  "monto": 18000.00, "fechaVencimiento": "2027-01-15", "notas": "opcional" }
```
`tipo`: `tenencia` | `placas` | `seguro` | `verificacion` | `otro`. Response 201.

### PUT /tramites/{id}
```json
{ "fechaVencimiento": "2027-01-15", "monto": 18500.00, "notas": "renovado" }
```
Response 200: TramiteVehiculoDTO.

### DELETE /tramites/{id} → 200 texto.

---

## GASTOS GENERALES

### GET /gastos-generales | GET /gastos-generales/{id}
### GET /gastos-generales/mes?mes=7&anio=2026
### GET /gastos-generales/anio?anio=2026
```json
[ { "id": 1, "fecha": "2026-07-05", "descripcion": "Papelería", "monto": 350.00 } ]
```

### POST /gastos-generales
```json
{ "fecha": "2026-07-05", "descripcion": "Papelería", "monto": 350.00 }
```

### PUT /gastos-generales/{id} — mismo shape, campos opcionales
### DELETE /gastos-generales/{id} → 200 texto.

---

## CALENDARIO

### GET /calendario?desde=2026-08-01&hasta=2026-08-31
```json
[ { "camionetaId": 1, "camionetaNombre": "Urvan 1", "estado": "activa",
    "ocupaciones": [
      { "viajeId": 10, "fechaInicio": "2026-08-10", "fechaFin": "2026-08-12",
        "clienteId": 1, "clienteNombre": "María López",
        "concepto": "Viaje a Acapulco", "estado": "apartado" } ] } ]
```
Incluye camionetas `en_taller` (con su estado, pa pintarlas bloqueadas).

---

## TOTALES

### GET /totales — acumulado
### GET /totales?anio=2026 — por año
### GET /totales?mes=7&anio=2026 — por mes
```json
{ "mes": 7, "anio": 2026, "ingresosTotal": 120000.00,
  "egresosViajes": 30000.00, "egresosCamionetas": 18000.00, "egresosGenerales": 4000.00,
  "egresosTotal": 52000.00, "neto": 68000.00, "pendientePorCobrar": 9000.00 }
```
`mes`/`anio` vienen `null` en modos año/acumulado según aplique. Las 3 cuentas de gasto van separadas: viajes / camionetas (mantenimiento+trámites) / generales.

---

## AVISOS

### GET /avisos
```json
{
  "mantenimientos": [
    { "camionetaId": 1, "camionetaNombre": "Urvan 1", "kmActual": 49600,
      "kmFaltantes": 400, "nivel": "400km", "tipo": "mantenimiento", "prioridad": 2 } ],
  "tramites": [
    { "tramiteId": 1, "camionetaId": 1, "camionetaNombre": "Urvan 1", "tipo": "seguro",
      "fechaVencimiento": "2026-08-10", "diasFaltantes": 14, "nivel": "15dias",
      "tipoAviso": "vencimiento", "prioridad": 2 } ],
  "total": 2
}
```
Avisos por km: faltando 500/400/300 y persiste si se pasó. Vencimientos: 30/15/10/5 días y persiste vencido. `prioridad` menor = más urgente.

### GET /avisos/mantenimientos — solo array mantenimientos
### GET /avisos/tramites — solo array trámites

---

## DASHBOARD

### GET /dashboard/mes?mes=7&anio=2026
### GET /dashboard/anio?anio=2026
### GET /dashboard/acumulado
```json
{ "periodo": "2026-07", "ingresosTotal": 120000.00, "egresosTotal": 52000.00,
  "netoTotal": 68000.00,
  "camionetas": [
    { "camionetaId": 1, "camionetaNombre": "Urvan 1", "ingresos": 70000.00,
      "egresos": 30000.00, "neto": 40000.00, "viajesCompletados": 6,
      "porcentajeUtilizacion": 45.16 } ] }
```
`porcentajeUtilizacion` solo viene en el modo `/mes`.
