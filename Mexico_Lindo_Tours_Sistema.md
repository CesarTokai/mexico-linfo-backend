# 📑 Mexico Lindo Tours — Documentación del Sistema

> Documento vivo. Base para construir el sistema. Se amplía con cada retroalimentación.
> **Última actualización:** 04/06/2026 · **Moneda:** MXN
> **Stack objetivo:** Vue 3 (frontend) · Spring Boot / Java (backend REST) · MySQL (base de datos)

---

## 1. La empresa

**Mexico Lindo Tours** presta servicio de viajes en camionetas Urvan NV350 (14 pasajeros).
Se gestiona todo alrededor de las unidades: viajes, cuentas, mantenimientos, refacciones, kilometraje, trámites (tenencia, placas, seguro), documentación, clientes, choferes y calendario.

---

## 2. Idea central del diseño (lo que evita complicarlo)

- El **calendario** NO es un módulo aparte: se deriva de los viajes (fechas + unidad).
- Los **historiales** (chofer, cliente, unidad) NO son tablas aparte: son **consultas** sobre los viajes y trámites ya guardados.
- Totales, km acumulado y avisos también **se calculan**; no se capturan dos veces.

---

## 3. Qué guarda el sistema (entidades / tablas MySQL)

### 3.1 `camioneta`
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| nombre | VARCHAR | Ximena / Libertad |
| modelo | VARCHAR | Urvan NV350 |
| capacidad | INT | 14 |
| km_actual | INT | se actualiza solo al cerrar viajes |
| intervalo_mantenimiento_km | INT | **PENDIENTE confirmar: ¿10,000 km?** |
| activa | BOOLEAN | baja lógica (conserva historial) |

### 3.2 `chofer`
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| nombre | VARCHAR | Ángel, Piza, Peñalosa… |
| telefono | VARCHAR | opcional |
| activo | BOOLEAN | entran y salen → baja lógica |

### 3.3 `cliente`
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| nombre | VARCHAR | |
| telefono | VARCHAR | opcional |
| notas | VARCHAR | opcional |

> Se promueve a tabla para poder llevar el **historial por cliente**.

### 3.4 `viaje`  ← el corazón
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| cliente_id | FK → cliente | |
| concepto | VARCHAR | ej. "Traslado al aeropuerto" |
| camioneta_id | FK → camioneta | |
| chofer_id | FK → chofer | |
| fecha_inicio | DATE | |
| fecha_fin | DATE | igual a inicio si es de 1 día |
| km_inicial | INT | |
| km_final | INT | |
| costo_total | DECIMAL(10,2) | ingreso del viaje |
| adelanto | DECIMAL(10,2) | abono recibido |
| estado_cobro | ENUM | pagado / pendiente / con_adelanto |

### 3.5 `gasto`  (desglose de cada viaje)
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| viaje_id | FK → viaje | |
| tipo | ENUM | caseta / gasolina / chofer / otros |
| descripcion | VARCHAR | ej. "Caseta Alpuyeca" |
| monto | DECIMAL(10,2) | |

> Un viaje tiene **muchos gastos** (1:N). Las casetas repetidas son renglones separados.

### 3.6 `mantenimiento`  (incluye refacciones)
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| camioneta_id | FK → camioneta | |
| fecha | DATE | |
| km_al_momento | INT | |
| tipo | ENUM | mantenimiento / refaccion |
| descripcion | VARCHAR | qué se hizo |
| costo | DECIMAL(10,2) | |

### 3.7 `tramite_vehiculo`  (tenencia, placas, seguro, verificación…)
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| camioneta_id | FK → camioneta | |
| tipo | ENUM | tenencia / placas / seguro / verificacion / otro |
| fecha_pago | DATE | cuándo se hizo |
| monto | DECIMAL(10,2) | cuánto se pagó |
| fecha_vencimiento | DATE | cuándo toca el siguiente |
| notas | VARCHAR | opcional |

> Modelo: "cuándo se hizo, cuánto, y cuándo toca el siguiente". El sistema avisa antes del vencimiento.

### 3.8 `usuario`  (acceso al sistema)
| Campo | Tipo | Notas |
|------|------|------|
| id | BIGINT PK | |
| nombre | VARCHAR | |
| correo | VARCHAR UNIQUE | login |
| password_hash | VARCHAR | encriptado (BCrypt) |
| activo | BOOLEAN | |

> Son 3 personas que gestionan. Todas con el mismo nivel de acceso (sin matriz de roles compleja).

---

## 4. Relaciones

- `cliente` 1 — N `viaje`
- `camioneta` 1 — N `viaje`
- `chofer` 1 — N `viaje`
- `viaje` 1 — N `gasto`
- `camioneta` 1 — N `mantenimiento`
- `camioneta` 1 — N `tramite_vehiculo`

---

## 5. Lo que se calcula (NO se guarda)

| Cálculo | Fórmula / origen |
|--------|---------|
| Km recorridos del viaje | `km_final − km_inicial` |
| Egresos del viaje | suma de sus `gasto.monto` |
| Neto del viaje | `costo_total − egresos` |
| Pendiente por cobrar | `costo_total − adelanto` |
| Total ganado (neto) | suma de netos de todos los viajes |
| Total gastado | suma de gastos (¿+ trámites? ver §12) |
| Total ingresos brutos | suma de `costo_total` |
| Cuentas por cobrar | suma de pendientes |
| Calendario | derivado de `viaje` |
| Historial de chofer | viajes del chofer + km manejados + total pagado a él |
| Historial de cliente | viajes del cliente + total pagado + pendientes |
| Historial de unidad | viajes + km + mantenimientos + trámites de esa camioneta |

---

## 6. Lógica automática

**Al cerrar un viaje (captura de `km_final`):**
1. `camioneta.km_actual = km_final`.
2. Compara contra último mantenimiento + `intervalo_mantenimiento_km`.
3. Si se acerca/pasa → avisa **"toca mantenimiento"**.

**Trámites:** si `fecha_vencimiento` se acerca → avisa **"por vencer: tenencia/seguro/etc."**.

---

## 7. Vistas del frontend (Vue 3)

1. **Calendario** — libre/ocupado por día; unidad, chofer y cliente.
2. **Viaje (alta/edición)** — ingreso, gastos desglosados, km; neto en vivo.
3. **Cuentas / totales** — ganado, gastado, brutos, por cobrar.
4. **Unidades** — km actual, aviso de mantenimiento, trámites por vencer.
5. **Mantenimientos** — historial y costos por camioneta.
6. **Choferes** — lista + historial de cada uno.
7. **Clientes** — lista + historial de cada uno.

---

## 8. API REST (núcleo, Spring Boot)

```
POST /auth/login            (devuelve token)
/usuarios            GET, POST, PUT
/camionetas          GET, POST, PUT
/choferes            GET, POST, PUT
/choferes/{id}/historial   GET
/clientes            GET, POST, PUT
/clientes/{id}/historial   GET
/viajes              GET, POST, PUT, DELETE
/viajes/{id}/gastos        GET, POST, DELETE
/mantenimientos      GET, POST
/tramites            GET, POST, PUT
/calendario          GET   (rango de fechas → ocupación)
/totales             GET   (acumulados)
```

---

## 9. Seguridad (simple y confiable)

- **Spring Security + JWT** (stateless): login devuelve token; las rutas protegidas lo exigen.
- Contraseñas con **BCrypt** (nunca en texto plano).
- 3 usuarios, **2 roles**: **ADMIN** (todo + gestión de usuarios + eliminar/modificar viajes finalizados) y **GESTOR** (operación diaria completa).
- Los **clientes no tienen sesión** — son registros, no usuarios del sistema.
- En el frontend: el token se guarda y se envía en cada petición (interceptor de axios).

> Es el estándar para Spring Boot: rápido, confiable y sin complicaciones extra.

---

## 10. Estructura del proyecto (encarpetado, simple)

**Backend — Spring Boot (por capas):**
```
com.mexicolindotours/
  config/       (configuración general)
  security/     (JWT, filtros, Spring Security)
  model/        (entidades JPA)
  repository/   (acceso a datos)
  service/      (lógica de negocio)
  controller/   (endpoints REST)
  dto/          (objetos de entrada/salida)
```

**Frontend — Vue 3 + Vite:**
```
src/
  views/        (páginas: Calendario, Viajes, Cuentas, Unidades, Choferes, Clientes)
  components/   (reutilizables)
  router/       (rutas)
  stores/       (Pinia: estado)
  services/     (axios: llamadas a la API)
  assets/
```

---

## 11. Glosario y reglas

- **Adelanto / abono** = pago anticipado del cliente. Suma al ingreso, reduce el pendiente.
- **Caseta** = peaje. Egreso.
- **Chofer** = quien maneja. Su pago es egreso.
- **Tenencia / placas / seguro / verificación** = trámites de la unidad, con costo y fecha de vencimiento.
- *(Se agregan reglas nuevas conforme se definan.)*

---

## 12. Fases de construcción

- **Fase 1 (ahora):** registrar viajes en este chat; afinar formato y reglas.
- **Fase 2:** app web con login (3 usuarios) — calendario + viajes + totales (entidades 3.1–3.5, 3.8).
- **Fase 3:** mantenimientos, trámites, historiales y reportes (entidades 3.6–3.7 + vistas).

---

## 13. Pendientes por definir

- [x] Intervalo de mantenimiento: **10,000 km confirmado**.
- [x] Trámites: **cuenta independiente por camioneta** (no entran al gasto de viajes).
- [ ] Km actual de Ximena y Libertad.
- [ ] ¿Contabilidad arranca desde cero o hay movimientos previos?
- [ ] ¿El rol GESTOR ve el dashboard de ganancias, o solo ADMIN?
- [x] Usuarios del sistema: **3 personas, mismo acceso.**

---

## 14. Registros

### Registro #1
04/06/2026 — Traslado aeropuerto — Cliente: Luis Valencia
Ingreso 5,000 · Egresos 2,348 (casetas 448 + gasolina 1,000 + chofer 900) · **Neto 2,652**
Camioneta / chofer / estado de cobro / km: *pendientes de confirmar.*

---

## 15. Reglas de negocio confirmadas (entrevista 20 preguntas)

**Viajes y cotización**
- A1. Precio del viaje: estimado con casetas/distancia/gasolina, pero **capturado manual**. El sistema no calcula precios.
- A2. Una renta = **un viaje por camioneta**. Si salen las 2 unidades, son 2 viajes (mismo cliente o distintos).
- A3. Un chofer por viaje, **reasignable** (los choferes a veces cancelan; puede ir el dueño).
- A4. El viaje nace con **contrato + adelanto** (o 50%). Si se cancela, **el adelanto no se devuelve** — es penalización por apartar la fecha. El viaje cancelado se conserva y su adelanto cuenta como ingreso.

**Cobros y pagos**
- B5. Práctica normal: **2 pagos** — apartado + liquidación (al comenzar o terminar). Se soportan abonos extra.
- B6. **El chofer se paga al final** del viaje.
- B7. **La empresa cubre todos los gastos** del viaje (no hay reembolsos). La gasolina se calcula manual (foto del nivel); el sistema solo registra montos.

**Calendario y disponibilidad**
- C8. Una camioneta puede regresar y salir **el mismo día** (raro pero permitido). La validación anti-doble-reserva solo bloquea traslapes reales.
- C9. **No existen reservas tentativas**: solo lo apartado con adelanto.
- C10. Se registra **disponibilidad de choferes por fecha** (opcional: a veces dan fechas, a veces solo se asigna).

**Mantenimiento y unidades**
- D11. Mantenimiento **cada 10,000 km** (editable por unidad).
- D12. Camioneta **en taller = bloqueada** en calendario, no asignable.
- D13. Avisos: faltando **500, 400 y 300 km**; si se pasa, **persiste** hasta hacerse el servicio.

**Trámites y documentos**
- E14. Tenencia, placas, seguro, verificación: **cuenta independiente por camioneta**. No entran al gasto de viajes.
- E15. Avisos de vencimiento: **30, 15, 10 y 5 días** antes; si vence, persiste.
- E16. Se vigila la **licencia de cada chofer** (vencimiento, mismos avisos).

**Clientes**
- F17. Los frecuentes se detectan **por historial**. Sin precios especiales ni crédito.

**Cuentas y reportes**
- G18. Totales **por mes, por año y acumulado**.
- G19. **Dashboard con ganancia por camioneta**.
- G20. Se registran **gastos generales del negocio** (aires, aguas…) no ligados a viaje ni unidad → tabla `gasto_general`.

**Tres cuentas de gasto separadas:** gastos de viajes · gastos por camioneta (mantenimiento + trámites) · gastos generales.

**Cambios al modelo derivados de la entrevista:**
- `viaje.estado`: apartado / en_curso / finalizado / cancelado (sustituye al campo adelanto + estado_cobro).
- Nueva tabla `pago` (1 viaje → N pagos: apartado, liquidación, abono).
- Nueva tabla `disponibilidad_chofer`.
- Nueva tabla `gasto_general`.
- `chofer.licencia_vencimiento`.
- `camioneta.estado`: activa / en_taller / baja.
- Validaciones: anti-doble-reserva (traslape de fechas por unidad) y coherencia de kilometraje.
