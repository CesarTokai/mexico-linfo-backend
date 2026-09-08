# CLAUDE.md — Instrucciones para Claude Code

Proyecto: **Mexico Lindo Tours** — sistema de gestión interna (viajes, unidades, cuentas) para una empresa de renta de camionetas Urvan NV350, más un **sitio público** de venta de tours por asiento y un **blog**.

Lee también `Mexico_Lindo_Tours_Sistema.md` (especificación original de la Fase 1-2) y `schema.sql` (base de datos, ya con las tablas del sitio público y el blog agregadas). Este archivo describe el estado **real** del sistema — donde la práctica se apartó de la especificación original, se anota explícitamente.

---

## Stack

- **Backend:** Java 17 · Spring Boot 3 · Spring Web · Spring Data JPA · Spring Security · Spring Mail (opcional) · MySQL 8 · Maven.
- **Frontend:** Vue 3 + Vite · Vue Router · Pinia · Axios. **No existe todavía** — solo hay backend.
- **Despliegue:** `Dockerfile` + `docker-compose.yml` en la raíz. `.env.example` documenta las variables; copiar a `.env` (nunca commitear).
- **Tests:** JUnit 5 + Mockito + AssertJ en `src/test/`. Unitarios con mocks, sin base de datos real — rápidos, corren con `mvn test`.

## Tres audiencias, NO dos roles

La especificación original decía "3 usuarios con 2 roles, los clientes no tienen acceso al sistema". **Eso cambió**: ahora conviven tres audiencias con seguridad separada en `SecurityConfig`, y la separación es explícita a propósito — nunca usar `authenticated()` a secas, porque con esa regla un cliente autenticado alcanzaría los endpoints internos:

- **Personal** (tabla `usuario`, roles `ADMIN` / `GESTOR`): toda la operación interna. `ADMIN` además gestiona usuarios, elimina/cancela viajes y modifica viajes finalizados. `GESTOR` no.
- **Cliente** (tabla `usuario_publico`, rol `CLIENTE`, separada a propósito de `usuario`): cuentas del sitio público — catálogo, favoritos, reservas. Un cliente **nunca** debe poder tocar la gestión interna, ni siquiera por un error de permisos; por eso es otra tabla, no un tercer valor del enum de roles del personal.
- **Público anónimo**: lectura del catálogo de tours (`/publico/paquetes`, `/publico/salidas/proximas`) y del blog (`/blog/**`), sin token.

**Auth interno:** JWT + BCrypt, stateless. El primer usuario se crea vía `/auth/crear-usuario` **sin token, solo si la tabla `usuario` está vacía** (bootstrap), y se fuerza a `ADMIN` para no bloquear la instalación; con la tabla ya poblada, el endpoint exige `ADMIN`.

**Auth del cliente:** registro con **nombre + teléfono + contraseña**; el correo es **opcional**, se completa después (`PUT /publico/mi/perfil/correo`). Login acepta correo *o* teléfono. El JWT del cliente usa el **teléfono** como sujeto — es el único dato que siempre existe. Decisión del dueño: las familias que solo manejan WhatsApp no tienen por qué dar un correo para reservar.

**Recuperación de contraseña** (solo clientes, solo si tienen correo registrado): `POST /publico/auth/recuperar` + `/restablecer`. Token de un solo uso, hasheado con SHA-256 (nunca en claro), vigente 60 min, misma respuesta exista o no el correo.

**Secreto JWT:** `JWT_SECRET` es obligatorio por variable de entorno — la aplicación **no arranca** si falta, si tiene menos de 64 bytes, o si es el valor de ejemplo (`change_in_production`). Generar con `openssl rand -base64 64`.

## Arquitectura

Backend por capas: `config/ security/ model/ repository/ service/ controller/ dto/`

Tres dominios dentro del mismo backend:
- **Gestión interna**: viajes, camionetas, choferes, clientes, pagos, gastos, mantenimientos, trámites, calendario, cuentas, avisos, usuarios.
- **Sitio público**: `paquete` (destino, precio de referencia) → `salida` (fecha concreta, cupo) → `reserva` (asientos apartados) → `favorito`. Un `paquete`/`salida` **no es** un `viaje`: el viaje es el registro interno con costos y datos que nunca deben salir al público.
- **Blog**: `post` / `categoria_post` / `etiqueta` (N:M), lectura pública sin token, administración bajo `/admin/blog/**`.

---

## Reglas de negocio — gestión interna (confirmadas por el dueño)

### Viajes
1. El **precio del viaje se captura manualmente** (el dueño lo estima con casetas/distancia/gasolina; el sistema NO lo calcula).
2. **Una renta privada = un viaje por camioneta.** Si salen las 2 unidades, son 2 registros de viaje. *(Coexiste con la venta por asiento del sitio público — ver más abajo; no son el mismo modelo.)*
3. **Un chofer por viaje**, reasignable en cualquier momento antes/al inicio. `chofer_id` puede ser NULL al apartar.
4. **Ciclo de vida:** `apartado → en_curso → finalizado`, o `cancelado`. Transiciones validadas: no se salta directo a `finalizado` (eso exige `km_final` vía el endpoint de finalizar), un viaje `en_curso` no regresa a `apartado`, y `finalizado`/`cancelado` son terminales.
   - Un viaje solo existe si hay contrato + adelanto (no hay reservas tentativas).
   - **Un viaje cancelado se EXCLUYE de ingresos y egresos en `/totales` y `/dashboard`** (se conserva como historial). *Esto reemplaza la regla original ("el adelanto no se devuelve y sus pagos cuentan como ingreso"): el dueño decidió simplificar y excluirlo del todo en vez de reconocer el adelanto — comportamiento distinto al de las reservas del sitio público (ver abajo), y es intencional, no un descuido.*

### Pagos y gastos
5. Pagos por viaje en tabla `pago`: práctica normal 2 pagos (`apartado` + `liquidacion`); soporta `abono` extra.
6. El chofer se paga al final del viaje (gasto tipo `chofer`).
7. Todos los gastos los cubre la empresa. La gasolina se calcula manual: el sistema solo registra montos.

### Calendario y disponibilidad
8. **Anti-doble-reserva de la camioneta**: valida traslape contra viajes NO cancelados **y contra salidas públicas no canceladas** — la misma unidad no se puede vender dos veces, sea por privado o por asiento. Vive en `DisponibilidadUnidadService`, único lugar que decide si una unidad está libre.
   - **Excepción C8, con un matiz importante** encontrado tras dos intentos fallidos: una unidad puede *regresar* el día X y *salir* el día X, pero el contacto solo cuenta como "regreso/salida" si **ambas** ocupaciones duran más de un día. Una renta de un solo día que coincide con el día en que un tour de varios días sale (o regresa) **sí** es choque real — un viaje de un día ocupa la jornada completa, no "regresa" ese día.
9. Camioneta `en_taller` = bloqueada: no asignable a viajes ni a salidas, visible como no disponible en el calendario. `baja` tampoco es asignable.
10. **Disponibilidad de chofer**: la tabla `disponibilidad_chofer` sigue siendo un registro **opcional** por fecha (si no hay registro, se puede asignar igual — regla C10 sin cambios). Lo que sí se agregó es la validación del **choque real**: un chofer no puede estar asignado a dos viajes/salidas que se traslapan en fecha (`DisponibilidadChoferService.verificarLibre`, misma lógica de traslape que la unidad).

### Kilometraje y mantenimiento
11. Validar `km_final > km_inicial` y `km_inicial >= camioneta.km_actual`.
12. Al finalizar un viaje: `camioneta.km_actual = km_final`.
13. Mantenimiento cada 10,000 km (editable por unidad), contado **desde el último servicio registrado** (no desde múltiplos del odómetro — bug corregido: antes el aviso desaparecía justo al pasarse el intervalo). Una `refaccion` no reinicia el ciclo, solo un `mantenimiento`. Avisos escalonados 500/400/300 km; pasado el intervalo, nivel `VENCIDO` que **persiste**.

### Trámites, licencias y documentos
14. Trámites (tenencia, placas, seguro, verificación) son cuenta propia de cada camioneta, NO entran al gasto de viajes.
15. Avisos de vencimiento de trámites: 30/15/10/5 días antes; si vence, persiste.
16. **Licencia de chofer**: mismos avisos (30/15/10/5, persistente) — implementados en `ChoferService` + expuestos en `GET /avisos/licencias`.

### Clientes
17. Clientes frecuentes se detectan por su historial. Sin precios especiales ni crédito.

### Cuentas y reportes
18. Totales por mes, año y acumulado.
19. Dashboard con ganancia por camioneta — **incluye la venta por asiento** que operó esa unidad (`ingresosSalidasPublicas` en el DTO), no solo los viajes privados.
20. Tabla `gasto_general` para gastos del negocio no ligados a viaje/unidad.

### Cálculos (no almacenar, derivar)
- Pagado del viaje = suma de `pago.monto`.
- Pendiente por cobrar = `costo_total − pagado` (viajes no cancelados) **+ saldo pendiente de reservas confirmadas** (`monto_total − monto_pagado`).
- Neto del viaje = ingresos − egresos.
- Historiales (chofer, cliente, unidad) = consultas sobre viajes/pagos/gastos **y sobre las salidas/reservas que operó esa unidad**.

### Técnicas
- Dinero: `DECIMAL`/`BigDecimal`, nunca float/double.
- Choferes/camionetas: baja lógica, nunca borrar.
- Calendario = consulta sobre `viaje` **y sobre `salida`** (una salida pública ocupa el calendario igual que una renta privada).

---

## Sitio público: venta de tours por asiento

Decisión del dueño (no estaba en la especificación original): además de la renta privada de camioneta completa, se vende **por asiento** en salidas programadas — como una agencia de tours.

### Modelo
- `paquete`: destino, descripción, precio de referencia por persona, categoría. Es el producto que ve el público; **no** lleva costos internos.
- `salida`: fecha concreta de un paquete, con cupo total. Puede llevar `camioneta` y `chofer` asignados (opcional, se completa después) — ambos validados contra doble-reserva real, igual que un viaje.
- `reserva`: apartado de N asientos por un cliente, con bloqueo pesimista sobre la salida para que dos personas no se lleven el mismo último lugar.
- `favorito`: marcado idempotente de un paquete por cliente.

### Reglas confirmadas
21. **Anticipo del 50%** (configurable, `RESERVAS_PORCENTAJE_ANTICIPO`): al apartar se calcula el anticipo; los asientos se aseguran (`confirmada`) en cuanto el personal verifica que se cubrió ese monto, no antes. El resto se liquida después con otro comprobante — subir un segundo comprobante **no degrada** una reserva ya confirmada.
22. **Pago por transferencia, sin pasarela**: el cliente sube su comprobante (imagen o PDF) y el personal lo verifica manualmente y confirma el monto recibido. Los comprobantes **no son públicos** aunque su nombre sea aleatorio — llevan datos bancarios, exigen sesión para verse.
23. **Reservas sin comprobante caducan solas** a las 48 h (`RESERVAS_HORAS_PARA_PAGAR`, configurable) y liberan el cupo, dejando escrito el motivo en la reserva.
24. **Cancelación: el anticipo no se devuelve, se queda como ingreso de la empresa** — misma política que los viajes privados, pero con una diferencia importante: una reserva **confirmada** reconoce la venta completa (`monto_total`) mientras el servicio sigue en pie; una reserva **cancelada** solo reconoce lo que **sí se cobró** (`monto_pagado`), no el total — el servicio no se va a dar, no tiene sentido contar la venta entera.
25. **La salida se cierra sola** (`cerrada`) cuando se agota el cupo, y **reabre sola** (`programada`) si se libera un asiento (cancelación). Nunca toca una salida `cancelada`. Una salida `cerrada` **sigue siendo visible** en el catálogo público con 0 disponibles — no desaparece, ni siquiera cuando el cierre lo causaron solo reservas pendientes de pago que aún pueden caducar.
26. **Reserva manual del personal** (`POST /admin/turismo/reservas/manual`): cubre las ventas que no pasan por el sitio — familias que solo usan WhatsApp, o que reservan **la camioneta completa** para un evento/festividad. Identifica al cliente por teléfono (crea la cuenta si no existía) y puede marcarse como ya cobrada de una vez. Reutiliza la misma lógica de cupo/anticipo del flujo público, no la duplica.

### Pendiente, no implementado
- Verificación de que el correo del cliente sea suyo (cualquiera puede registrarse con un correo ajeno).
- Notificaciones por correo: existen y funcionan (`NotificacionService`), pero están **apagadas por defecto** (`app.notificaciones.habilitado=false`) — decisión del dueño de dejarlas opcionales por ahora.

---

## Blog

Lectura pública sin token (`GET /blog/**`); administración bajo `/admin/blog/**` con token de personal. Una categoría por post (alimenta el sidebar con conteo), muchas etiquetas por post (N:M). Slugs automáticos que respetan acentos. Imágenes con nombre aleatorio, validadas por tipo/extensión.

---

## Seguridad — puntos que ya mordieron una vez

- **Nunca** `anyRequest().authenticated()` a secas: siempre roles explícitos (`hasAnyRole("ADMIN","GESTOR")` para lo interno, `hasRole("CLIENTE")` para `/publico/mi/**`).
- **Nunca** secretos ni contraseñas en `application.properties` — todo por variable de entorno (`${VAR:valor_por_defecto}`), y sin valor por defecto cuando el valor es sensible.
- `docker-compose.yml` debe fijar `name:` explícito — sin eso, Compose usa el nombre de la carpeta y puede colisionar con otro proyecto del mismo servidor.
- Al agregar un endpoint nuevo, decidir explícitamente a cuál de las tres audiencias pertenece; no asumir que "requiere login" alcanza.

---

## Qué NO hacer

- No colapsar las tres audiencias (personal / cliente / público) en un solo esquema de permisos. Un cliente jamás debe alcanzar rutas internas.
- No calcular precios de viaje ni gasolina automáticamente (es manual por decisión del dueño).
- No usar float para dinero. No borrar registros físicamente (baja lógica).
- No mezclar el modelo de `viaje` (renta privada) con el de `paquete`/`salida` (venta por asiento) — son productos distintos con reglas de cancelación e ingreso distintas, aunque compartan camioneta y chofer.
- No agregar una pasarela de pagos sin que el dueño lo pida — la política actual es transferencia + comprobante verificado a mano, a propósito, para no complicar algo simple.
- Antes de cambiar una regla de traslape de fechas, escribir el caso límite como test primero: esta regla ya se equivocó dos veces en los bordes (día de contacto, ocupación de un solo día).
- Ante ambigüedad de negocio: preguntar al dueño, no asumir.
