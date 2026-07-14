# CLAUDE.md — Instrucciones para Claude Code

Proyecto: **Mexico Lindo Tours** — sistema de gestión de viajes, unidades y cuentas para una empresa de renta de camionetas Urvan NV350.

Lee también `Mexico_Lindo_Tours_Sistema.md` (especificación completa) y `schema.sql` v2 (base de datos). Este archivo dice **qué construir y en qué orden**.

---

## Stack

- **Backend:** Java 17+ · Spring Boot 3 · Spring Web · Spring Data JPA · Spring Security · MySQL · Maven.
- **Frontend:** Vue 3 + Vite · Vue Router · Pinia · Axios.
- **Auth:** JWT + BCrypt. Stateless. 3 usuarios con **2 roles**:
  - **ADMIN**: todo + crear/desactivar usuarios + eliminar viajes / modificar finalizados.
  - **GESTOR**: toda la operación diaria (viajes, pagos, gastos, calendario, mantenimientos, trámites, clientes, choferes). NO gestiona usuarios ni elimina/modifica viajes finalizados.
  - **Dashboard de ganancias: PENDIENTE definir si GESTOR lo ve** (no hardcodear; dejarlo detrás de un permiso simple configurable).
  - Los clientes NO tienen acceso al sistema (solo son registros).

## Arquitectura

Backend por capas: `config/ security/ model/ repository/ service/ controller/ dto/`
Frontend: `views/ components/ router/ stores/ services/ assets/`

---

## Reglas de negocio (TODAS confirmadas por el dueño — NO inventar)

### Viajes
1. El **precio del viaje se captura manualmente** (el dueño lo estima con casetas/distancia/gasolina, pero el sistema NO lo calcula).
2. **Una renta = un viaje por camioneta.** Si salen las 2 unidades (mismo cliente o clientes distintos), son 2 registros de viaje.
3. **Un chofer por viaje**, pero **reasignable** en cualquier momento antes/al inicio (los choferes a veces cancelan). `chofer_id` puede ser NULL al apartar.
4. **Ciclo de vida del viaje:** `apartado → en_curso → finalizado`, o `cancelado`.
   - Un viaje **solo existe si hay contrato + adelanto** (no hay reservas tentativas).
   - Si se **cancela**: el adelanto **NO se devuelve** (penalización por apartar fecha). El viaje se conserva con estado `cancelado` y sus pagos recibidos **cuentan como ingreso**.

### Pagos y gastos
5. Pagos por viaje en tabla `pago`: práctica normal **2 pagos** (`apartado` + `liquidacion`); soporta `abono` extra.
6. **El chofer se paga al final del viaje** (gasto tipo `chofer`).
7. **Todos los gastos los cubre la empresa** (no hay reembolsos al chofer). La gasolina se calcula manual, el sistema solo registra montos.

### Calendario y disponibilidad
8. **Anti-doble-reserva:** al crear/editar un viaje, validar que la camioneta no tenga traslape de fechas con otro viaje NO cancelado. **Excepción permitida:** un viaje puede terminar el día X y otro iniciar el mismo día X con la misma unidad.
9. Camioneta `en_taller` = **bloqueada**: no asignable a viajes y visible como no disponible en el calendario.
10. **Disponibilidad de choferes** (tabla `disponibilidad_chofer`): registro opcional por fecha. Si no hay registro, se puede asignar de todas formas (a veces solo se asigna directo).

### Kilometraje y mantenimiento
11. Validar: `km_final > km_inicial` y `km_inicial >= camioneta.km_actual` (avisar si no cuadra).
12. Al **finalizar** un viaje: `camioneta.km_actual = km_final`.
13. **Mantenimiento cada 10,000 km** (campo editable por unidad). **Avisos escalonados: faltando 500, 400 y 300 km**; si se pasa el intervalo, **el aviso persiste** hasta registrar el mantenimiento.

### Trámites y documentos
14. Trámites (tenencia, placas, seguro, verificación) son **cuenta propia de cada camioneta**. NO entran al gasto de viajes.
15. **Avisos de vencimiento: 30, 15, 10 y 5 días antes**; si vence, **persiste** hasta renovarse.
16. **Licencia de chofer:** campo `licencia_vencimiento`; mismos avisos de vencimiento.

### Clientes
17. Clientes frecuentes se detectan **por su historial** (número de viajes). Sin precios especiales ni crédito.

### Cuentas y reportes
18. Totales **por mes, por año y acumulado**.
19. **Dashboard** con ganancia por camioneta (ingresos, gastos, neto de cada unidad).
20. Tabla `gasto_general` para gastos del negocio no ligados a viaje/unidad.

### Cálculos (no almacenar, derivar)
- Pagado del viaje = suma de `pago.monto`
- Pendiente por cobrar = `costo_total − pagado` (solo viajes no cancelados)
- Egresos del viaje = suma de `gasto.monto`
- Neto del viaje = ingresos − egresos
- Km recorridos = `km_final − km_inicial`
- Historiales (chofer, cliente, unidad) = consultas sobre viajes/pagos/gastos
- Tres cuentas separadas: **gastos de viajes**, **gastos por camioneta** (mantenimiento + trámites), **gastos generales**

### Técnicas
- Dinero: `DECIMAL`/`BigDecimal`, nunca float/double.
- Choferes/camionetas: baja lógica, nunca borrar (conserva historial).
- Calendario = consulta sobre `viaje`, no tabla.
- **Respaldo:** documentar/incluir cron con `mysqldump` diario.

---

## Orden de construcción

**Fase 1 — Base**
1. Proyecto Spring Boot + MySQL (`schema.sql` v2).
2. Entidades JPA + repositorios (10 tablas).
3. Login JWT + BCrypt + creación del primer usuario.

**Fase 2 — Núcleo operativo**
4. CRUD camioneta, chofer, cliente.
5. CRUD viaje con `pago` y `gasto` anidados + ciclo de estados + validación anti-doble-reserva + validación de km.
6. `GET /calendario?desde&hasta` (ocupación por unidad, incluye en_taller).
7. `GET /totales?mes&anio` (por mes, año, acumulado; 3 cuentas de gasto separadas).
8. Frontend: login, calendario, alta/edición de viaje, cuentas.

**Fase 3 — Gestión y dashboard**
9. Mantenimientos + avisos por km (500/400/300, persistente).
10. Trámites + licencias + avisos por vencimiento (30/15/10/5, persistente).
11. Disponibilidad de choferes. Gastos generales.
12. Historiales (chofer, cliente, unidad) + dashboard ganancia por camioneta.

---

## Endpoints núcleo

```
POST /auth/login
/usuarios /camionetas /choferes /clientes        CRUD
/choferes/{id}/historial  /clientes/{id}/historial  /camionetas/{id}/historial
/choferes/{id}/disponibilidad   GET, POST
/viajes  CRUD  + /viajes/{id}/pagos  + /viajes/{id}/gastos  + /viajes/{id}/estado (PUT)
/mantenimientos  /tramites  /gastos-generales     GET, POST, PUT
/calendario   GET (rango)
/totales      GET (mes/año/acumulado)
/avisos       GET (mantenimientos por km + vencimientos de trámites y licencias)
/dashboard    GET (ganancia por camioneta)
```

---

## Qué NO hacer

- Solo los 2 roles definidos (ADMIN/GESTOR). No agregar permisos granulares por módulo.
- No agregar módulos ni campos fuera del schema/especificación.
- No calcular precios de viaje ni gasolina automáticamente (es manual por decisión del dueño).
- No usar float para dinero. No borrar registros físicamente.
- Ante ambigüedad: preguntar al dueño, no asumir.
