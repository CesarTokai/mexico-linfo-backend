-- =====================================================================
-- DATOS DE PRUEBA — Mexico Lindo Tours
-- Verificado contra la BD REAL generada por Hibernate (ddl-auto=update)
-- Todas las tablas: created_at NOT NULL SIN default -> se pasa NOW() explícito
--
-- USUARIOS: NO van por SQL (hash BCrypt lo genera la app). Con backend corriendo:
--   curl -X POST "http://localhost:8080/auth/crear-usuario?nombre=Admin&correo=admin@mexicolindo.com&password=admin123&rol=ADMIN"
--   curl -X POST "http://localhost:8080/auth/crear-usuario?nombre=Gestor&correo=gestor@mexicolindo.com&password=admin123&rol=GESTOR"
-- =====================================================================

USE mexico_lindo_tours;

-- ---------------------------------------------------------------------
-- 0. LIMPIEZA de datos del script anterior (usuarios con hash falso,
--    camionetas URVAN y choferes que no van)
-- ---------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM gasto;
DELETE FROM pago;
DELETE FROM viaje;
DELETE FROM disponibilidad_chofer;
DELETE FROM mantenimiento;
DELETE FROM tramite_vehiculo;
DELETE FROM gasto_general;
DELETE FROM cliente;
DELETE FROM chofer;
DELETE FROM camioneta;
DELETE FROM usuario WHERE correo IN ('admin@mexicolindo.com', 'gestor@mexicolindo.com');
-- ^ borra los usuarios con hash falso; recréalos via API (ver arriba)

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. CAMIONETAS (las 2 reales del negocio)
-- ---------------------------------------------------------------------
INSERT INTO camioneta (nombre, modelo, capacidad, km_actual, intervalo_mantenimiento_km, estado, created_at, updated_at) VALUES
  ('Ximena',   'Urvan NV350', 14, 5230, 10000, 'activa', NOW(), NOW()),
  ('Libertad', 'Urvan NV350', 14, 8750, 10000, 'activa', NOW(), NOW());

-- ---------------------------------------------------------------------
-- 2. CHOFERES
-- ---------------------------------------------------------------------
INSERT INTO chofer (nombre, telefono, licencia_vencimiento, activo, created_at, updated_at) VALUES
  ('Ángel',    '5551234567', '2027-03-31', TRUE, NOW(), NOW()),
  ('Piza',     '5559876543', '2026-08-15', TRUE, NOW(), NOW()),
  ('Peñalosa', '5555555555', '2026-07-25', TRUE, NOW(), NOW());

-- ---------------------------------------------------------------------
-- 3. CLIENTES (columnas reales: nombre, telefono, notas)
-- ---------------------------------------------------------------------
INSERT INTO cliente (nombre, telefono, notas, created_at, updated_at) VALUES
  ('Viajes Corporativos S.A.', '5551111111', 'Cliente VIP, pagos puntuales', NOW(), NOW()),
  ('Turismo Beach Tours',      '5552222222', 'Viajes a playas',              NOW(), NOW()),
  ('Agencia de Eventos MX',    '5553333333', 'Bodas y eventos',              NOW(), NOW()),
  ('Hotel La Hacienda',        '5554444444', 'Traslados de huéspedes',       NOW(), NOW());

-- ---------------------------------------------------------------------
-- 4. VIAJES
-- ---------------------------------------------------------------------
INSERT INTO viaje (cliente_id, camioneta_id, chofer_id, concepto, fecha_inicio, fecha_fin, km_inicial, km_final, costo_total, estado, notas, created_at, updated_at) VALUES
  ((SELECT id FROM cliente WHERE nombre='Viajes Corporativos S.A.'),
   (SELECT id FROM camioneta WHERE nombre='Ximena'),
   (SELECT id FROM chofer WHERE nombre='Ángel'),
   'Viaje a Cancún', '2026-07-10', '2026-07-12', 5000, 5230, 3500.00, 'finalizado', 'Completado sin incidentes', NOW(), NOW()),

  ((SELECT id FROM cliente WHERE nombre='Viajes Corporativos S.A.'),
   (SELECT id FROM camioneta WHERE nombre='Ximena'),
   (SELECT id FROM chofer WHERE nombre='Ángel'),
   'Viaje a Playa del Carmen', '2026-07-20', '2026-07-22', NULL, NULL, 3000.00, 'apartado', 'Adelanto pagado', NOW(), NOW()),

  ((SELECT id FROM cliente WHERE nombre='Turismo Beach Tours'),
   (SELECT id FROM camioneta WHERE nombre='Libertad'),
   (SELECT id FROM chofer WHERE nombre='Piza'),
   'Tour Tulum y Xel-Há', '2026-07-13', '2026-07-15', 8500, 8750, 4000.00, 'finalizado', 'Grupo satisfecho', NOW(), NOW()),

  ((SELECT id FROM cliente WHERE nombre='Agencia de Eventos MX'),
   (SELECT id FROM camioneta WHERE nombre='Libertad'),
   (SELECT id FROM chofer WHERE nombre='Piza'),
   'Traslado boda', '2026-07-16', '2026-07-16', 8750, NULL, 2500.00, 'en_curso', 'Boda en hacienda', NOW(), NOW()),

  ((SELECT id FROM cliente WHERE nombre='Hotel La Hacienda'),
   (SELECT id FROM camioneta WHERE nombre='Ximena'),
   (SELECT id FROM chofer WHERE nombre='Peñalosa'),
   'Traslado huéspedes', '2026-07-25', '2026-07-25', NULL, NULL, 1500.00, 'apartado', 'Reserva hotel', NOW(), NOW());

-- ---------------------------------------------------------------------
-- 5. PAGOS (tipo: apartado/liquidacion/abono)
-- ---------------------------------------------------------------------
INSERT INTO pago (viaje_id, tipo, fecha, monto, metodo, notas, created_at) VALUES
  ((SELECT id FROM viaje WHERE concepto='Viaje a Cancún'),           'apartado',    '2026-07-05', 1750.00, 'transferencia', '50% adelanto',  NOW()),
  ((SELECT id FROM viaje WHERE concepto='Viaje a Cancún'),           'liquidacion', '2026-07-12', 1750.00, 'efectivo',      'Saldo completo', NOW()),
  ((SELECT id FROM viaje WHERE concepto='Viaje a Playa del Carmen'), 'apartado',    '2026-07-14', 1500.00, 'transferencia', 'Adelanto',       NOW()),
  ((SELECT id FROM viaje WHERE concepto='Tour Tulum y Xel-Há'),      'apartado',    '2026-07-08', 2000.00, 'tarjeta',       '50% adelanto',   NOW()),
  ((SELECT id FROM viaje WHERE concepto='Tour Tulum y Xel-Há'),      'liquidacion', '2026-07-15', 2000.00, 'efectivo',      'Saldo',          NOW()),
  ((SELECT id FROM viaje WHERE concepto='Traslado boda'),            'apartado',    '2026-07-10', 1250.00, 'transferencia', 'Adelanto',       NOW());

-- ---------------------------------------------------------------------
-- 6. GASTOS (tipo: caseta/gasolina/chofer/otros)
-- ---------------------------------------------------------------------
INSERT INTO gasto (viaje_id, tipo, descripcion, fecha, monto, notas, created_at) VALUES
  ((SELECT id FROM viaje WHERE concepto='Viaje a Cancún'),      'gasolina', 'Gasolina Pemex',       '2026-07-10', 350.00, '230 km recorridos', NOW()),
  ((SELECT id FROM viaje WHERE concepto='Viaje a Cancún'),      'caseta',   'Casetas autopista',    '2026-07-10', 120.00, 'Ida y vuelta',      NOW()),
  ((SELECT id FROM viaje WHERE concepto='Viaje a Cancún'),      'chofer',   'Pago chofer Ángel',    '2026-07-12', 500.00, 'Pago al finalizar', NOW()),
  ((SELECT id FROM viaje WHERE concepto='Tour Tulum y Xel-Há'), 'gasolina', 'Gasolina Pemex',       '2026-07-13', 300.00, '250 km',            NOW()),
  ((SELECT id FROM viaje WHERE concepto='Tour Tulum y Xel-Há'), 'caseta',   'Casetas Quintana Roo', '2026-07-13', 150.00, NULL,                NOW()),
  ((SELECT id FROM viaje WHERE concepto='Tour Tulum y Xel-Há'), 'chofer',   'Pago chofer Piza',     '2026-07-15', 600.00, 'Pago al finalizar', NOW()),
  ((SELECT id FROM viaje WHERE concepto='Traslado boda'),       'gasolina', 'Gasolina',             '2026-07-16', 100.00, '100 km',            NOW()),
  ((SELECT id FROM viaje WHERE concepto='Traslado boda'),       'otros',    'Lavado de unidad',     '2026-07-16', 150.00, NULL,                NOW());

-- ---------------------------------------------------------------------
-- 7. MANTENIMIENTOS (tipo: mantenimiento/refaccion)
-- ---------------------------------------------------------------------
INSERT INTO mantenimiento (camioneta_id, fecha, km_al_momento, tipo, descripcion, costo, created_at) VALUES
  ((SELECT id FROM camioneta WHERE nombre='Ximena'),   '2026-06-15', 5000, 'mantenimiento', 'Cambio de aceite y filtros', 500.00,  NOW()),
  ((SELECT id FROM camioneta WHERE nombre='Libertad'), '2026-06-01', 8500, 'mantenimiento', 'Revisión general completa', 1200.00,  NOW()),
  ((SELECT id FROM camioneta WHERE nombre='Libertad'), '2026-07-10', 8700, 'refaccion',     'Cambio de balatas',          600.00,  NOW());

-- ---------------------------------------------------------------------
-- 8. TRÁMITES (tipo: tenencia/placas/seguro/verificacion/otro)
--    Fechas pensadas para generar avisos HOY (2026-07-16)
-- ---------------------------------------------------------------------
INSERT INTO tramite_vehiculo (camioneta_id, tipo, fecha_pago, monto, fecha_vencimiento, notas, created_at) VALUES
  ((SELECT id FROM camioneta WHERE nombre='Ximena'),   'tenencia',     '2026-01-15', 1200.00, '2027-01-15', 'Tenencia 2026',                        NOW()),
  ((SELECT id FROM camioneta WHERE nombre='Ximena'),   'seguro',       '2026-01-20', 3500.00, '2026-07-30', 'Por vencer en 14 días — genera aviso', NOW()),
  ((SELECT id FROM camioneta WHERE nombre='Libertad'), 'tenencia',     '2026-02-01', 1200.00, '2027-02-01', 'Tenencia 2026',                        NOW()),
  ((SELECT id FROM camioneta WHERE nombre='Libertad'), 'verificacion', '2026-03-15',  250.00, '2026-07-20', 'Vence en 4 días — aviso urgente',      NOW()),
  ((SELECT id FROM camioneta WHERE nombre='Libertad'), 'placas',       '2025-12-01',  800.00, '2026-07-10', 'VENCIDO — aviso persistente',          NOW());

-- ---------------------------------------------------------------------
-- 9. DISPONIBILIDAD CHOFERES
-- ---------------------------------------------------------------------
INSERT INTO disponibilidad_chofer (chofer_id, fecha, disponible, notas, created_at) VALUES
  ((SELECT id FROM chofer WHERE nombre='Ángel'),    '2026-07-20', TRUE,  'Disponible todo el día', NOW()),
  ((SELECT id FROM chofer WHERE nombre='Ángel'),    '2026-07-21', TRUE,  'Disponible',             NOW()),
  ((SELECT id FROM chofer WHERE nombre='Piza'),     '2026-07-20', FALSE, 'Descanso',               NOW()),
  ((SELECT id FROM chofer WHERE nombre='Piza'),     '2026-07-21', TRUE,  'Disponible',             NOW()),
  ((SELECT id FROM chofer WHERE nombre='Peñalosa'), '2026-07-25', TRUE,  'Disponible',             NOW());

-- ---------------------------------------------------------------------
-- 10. GASTOS GENERALES
-- ---------------------------------------------------------------------
INSERT INTO gasto_general (fecha, descripcion, monto, created_at) VALUES
  ('2026-07-01', 'Renta de oficina - Julio',        5000.00, NOW()),
  ('2026-07-05', 'Servicios (luz, agua, internet)', 2000.00, NOW()),
  ('2026-07-10', 'Seguros generales empresa',       1500.00, NOW()),
  ('2026-07-15', 'Mantenimiento oficina',            800.00, NOW()),
  ('2026-07-20', 'Suministros y papelería',          500.00, NOW());

-- =====================================================================
-- VERIFICACIÓN
-- =====================================================================
SELECT 'usuario' AS tabla, COUNT(*) AS total FROM usuario
UNION ALL SELECT 'camioneta',             COUNT(*) FROM camioneta
UNION ALL SELECT 'chofer',                COUNT(*) FROM chofer
UNION ALL SELECT 'cliente',               COUNT(*) FROM cliente
UNION ALL SELECT 'viaje',                 COUNT(*) FROM viaje
UNION ALL SELECT 'pago',                  COUNT(*) FROM pago
UNION ALL SELECT 'gasto',                 COUNT(*) FROM gasto
UNION ALL SELECT 'mantenimiento',         COUNT(*) FROM mantenimiento
UNION ALL SELECT 'tramite_vehiculo',      COUNT(*) FROM tramite_vehiculo
UNION ALL SELECT 'disponibilidad_chofer', COUNT(*) FROM disponibilidad_chofer
UNION ALL SELECT 'gasto_general',         COUNT(*) FROM gasto_general;
