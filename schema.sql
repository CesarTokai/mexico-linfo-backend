-- =====================================================================
-- Mexico Lindo Tours — Esquema de base de datos (MySQL 8+)  v2
-- Codificación utf8mb4, motor InnoDB, dinero en DECIMAL(10,2)
-- Actualizado tras entrevista de lógica de negocio (20 preguntas)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS mexico_lindo_tours
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mexico_lindo_tours;

-- ---------------------------------------------------------------------
-- Usuarios del sistema (3 personas): 1 ADMIN + 2 GESTOR
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(120) NOT NULL,
  correo        VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,          -- BCrypt
  rol           ENUM('ADMIN','GESTOR') NOT NULL DEFAULT 'GESTOR',
  activo        BOOLEAN NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Camionetas
-- estado: activa (asignable) / en_taller (bloqueada en calendario) / baja
-- ---------------------------------------------------------------------
CREATE TABLE camioneta (
  id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre                      VARCHAR(60) NOT NULL,        -- Ximena / Libertad
  modelo                      VARCHAR(60) NOT NULL DEFAULT 'Urvan NV350',
  capacidad                   INT NOT NULL DEFAULT 14,
  km_actual                   INT NOT NULL DEFAULT 0,
  intervalo_mantenimiento_km  INT NOT NULL DEFAULT 10000,  -- confirmado: cada 10,000 km
  estado                      ENUM('activa','en_taller','baja') NOT NULL DEFAULT 'activa',
  created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Choferes (entran y salen -> baja lógica; se vigila licencia)
-- ---------------------------------------------------------------------
CREATE TABLE chofer (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre               VARCHAR(120) NOT NULL,
  telefono             VARCHAR(30) NULL,
  licencia_vencimiento DATE NULL,               -- para aviso de licencia vigente
  activo               BOOLEAN NOT NULL DEFAULT TRUE,
  created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Disponibilidad de choferes (opcional: a veces dan fechas, a veces no)
-- ---------------------------------------------------------------------
CREATE TABLE disponibilidad_chofer (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  chofer_id  BIGINT NOT NULL,
  fecha      DATE NOT NULL,
  disponible BOOLEAN NOT NULL DEFAULT TRUE,
  notas      VARCHAR(160) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_disp_chofer FOREIGN KEY (chofer_id) REFERENCES chofer(id),
  UNIQUE KEY uq_chofer_fecha (chofer_id, fecha)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Clientes (para historial; los frecuentes se detectan por historial)
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(160) NOT NULL,
  telefono   VARCHAR(30) NULL,
  notas      VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Viajes (núcleo)
-- estado: apartado (contrato + adelanto) -> en_curso -> finalizado
--         cancelado (el adelanto NO se devuelve: queda como ingreso/penalización)
-- Regla: una renta = un viaje por camioneta (si van 2 unidades, son 2 viajes)
-- ---------------------------------------------------------------------
CREATE TABLE viaje (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  cliente_id    BIGINT NOT NULL,
  camioneta_id  BIGINT NOT NULL,
  chofer_id     BIGINT NULL,                    -- reasignable; puede definirse después del apartado
  concepto      VARCHAR(200) NOT NULL,
  fecha_inicio  DATE NOT NULL,
  fecha_fin     DATE NOT NULL,                  -- = inicio si es de 1 día
  km_inicial    INT NULL,
  km_final      INT NULL,
  costo_total   DECIMAL(10,2) NOT NULL DEFAULT 0,   -- precio capturado manualmente
  estado        ENUM('apartado','en_curso','finalizado','cancelado') NOT NULL DEFAULT 'apartado',
  notas         VARCHAR(255) NULL,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_viaje_cliente   FOREIGN KEY (cliente_id)   REFERENCES cliente(id),
  CONSTRAINT fk_viaje_camioneta FOREIGN KEY (camioneta_id) REFERENCES camioneta(id),
  CONSTRAINT fk_viaje_chofer    FOREIGN KEY (chofer_id)    REFERENCES chofer(id)
) ENGINE=InnoDB;

CREATE INDEX idx_viaje_fechas    ON viaje (fecha_inicio, fecha_fin);
CREATE INDEX idx_viaje_camioneta ON viaje (camioneta_id);
CREATE INDEX idx_viaje_estado    ON viaje (estado);

-- ---------------------------------------------------------------------
-- Pagos del viaje (práctica normal: 2 pagos -> apartado + liquidación)
-- El "adelanto" es el pago tipo 'apartado'. Soporta abonos extra si pasa.
-- ---------------------------------------------------------------------
CREATE TABLE pago (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  viaje_id   BIGINT NOT NULL,
  tipo       ENUM('apartado','liquidacion','abono') NOT NULL,
  fecha      DATE NOT NULL,
  monto      DECIMAL(10,2) NOT NULL,
  notas      VARCHAR(160) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pago_viaje FOREIGN KEY (viaje_id) REFERENCES viaje(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Gastos del viaje (los cubre la empresa; el chofer se paga al final)
-- ---------------------------------------------------------------------
CREATE TABLE gasto (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  viaje_id    BIGINT NOT NULL,
  tipo        ENUM('caseta','gasolina','chofer','otros') NOT NULL,
  descripcion VARCHAR(160) NULL,
  monto       DECIMAL(10,2) NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_gasto_viaje FOREIGN KEY (viaje_id) REFERENCES viaje(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Mantenimientos y refacciones (cuenta propia de cada camioneta)
-- ---------------------------------------------------------------------
CREATE TABLE mantenimiento (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  camioneta_id  BIGINT NOT NULL,
  fecha         DATE NOT NULL,
  km_al_momento INT NULL,
  tipo          ENUM('mantenimiento','refaccion') NOT NULL,
  descripcion   VARCHAR(200) NULL,
  costo         DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_mant_camioneta FOREIGN KEY (camioneta_id) REFERENCES camioneta(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Trámites de la unidad: tenencia, placas, seguro, verificación
-- Cuenta propia de cada camioneta (NO entra al gasto de viajes)
-- ---------------------------------------------------------------------
CREATE TABLE tramite_vehiculo (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  camioneta_id      BIGINT NOT NULL,
  tipo              ENUM('tenencia','placas','seguro','verificacion','otro') NOT NULL,
  fecha_pago        DATE NOT NULL,
  monto             DECIMAL(10,2) NOT NULL DEFAULT 0,
  fecha_vencimiento DATE NULL,            -- cuándo toca el siguiente
  notas             VARCHAR(255) NULL,
  created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_tramite_camioneta FOREIGN KEY (camioneta_id) REFERENCES camioneta(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Gastos generales del negocio (no ligados a viaje ni a camioneta)
-- Ej.: aires acondicionados, aguas, papelería
-- ---------------------------------------------------------------------
CREATE TABLE gasto_general (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  fecha       DATE NOT NULL,
  descripcion VARCHAR(200) NOT NULL,
  monto       DECIMAL(10,2) NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================================
-- DATOS INICIALES (semilla)
-- =====================================================================
INSERT INTO camioneta (nombre, modelo, capacidad, intervalo_mantenimiento_km) VALUES
  ('Ximena',   'Urvan NV350', 14, 10000),
  ('Libertad', 'Urvan NV350', 14, 10000);

INSERT INTO chofer (nombre) VALUES
  ('Ángel'), ('Piza'), ('Peñalosa');

-- Nota: el usuario admin se crea desde la app (BCrypt), no se inserta hash a mano.

-- ============================================================
-- BLOG: categorias, etiquetas y posts
-- ============================================================

CREATE TABLE IF NOT EXISTS categoria_post (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80)  NOT NULL,
    slug         VARCHAR(100) NOT NULL UNIQUE,
    descripcion  VARCHAR(255),
    created_at   DATETIME(6)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS etiqueta (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(60) NOT NULL,
    slug         VARCHAR(80) NOT NULL UNIQUE,
    created_at   DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo             VARCHAR(200) NOT NULL,
    slug               VARCHAR(220) NOT NULL UNIQUE,
    resumen            VARCHAR(500),
    contenido          LONGTEXT     NOT NULL,
    imagen_url         VARCHAR(500),
    autor              VARCHAR(120) NOT NULL,
    categoria_id       BIGINT,
    estado             ENUM('borrador','publicado') NOT NULL DEFAULT 'borrador',
    fecha_publicacion  DATE,
    created_at         DATETIME(6)  NOT NULL,
    updated_at         DATETIME(6)  NOT NULL,
    CONSTRAINT fk_post_categoria FOREIGN KEY (categoria_id) REFERENCES categoria_post(id),
    INDEX idx_post_estado_fecha (estado, fecha_publicacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post_etiqueta (
    post_id      BIGINT NOT NULL,
    etiqueta_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, etiqueta_id),
    CONSTRAINT fk_pe_post     FOREIGN KEY (post_id)     REFERENCES post(id)     ON DELETE CASCADE,
    CONSTRAINT fk_pe_etiqueta FOREIGN KEY (etiqueta_id) REFERENCES etiqueta(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SITIO PUBLICO: paquetes, salidas, reservas y favoritos
-- ============================================================

CREATE TABLE IF NOT EXISTS usuario_publico (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(120) NOT NULL,
    correo         VARCHAR(160) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    telefono       VARCHAR(30),
    activo         BIT(1)       NOT NULL DEFAULT b'1',
    created_at     DATETIME(6)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS paquete (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo              VARCHAR(200)   NOT NULL,
    slug                VARCHAR(220)   NOT NULL UNIQUE,
    resumen             VARCHAR(500),
    descripcion         LONGTEXT,
    destino             VARCHAR(120)   NOT NULL,
    categoria           VARCHAR(120),
    imagen_url          VARCHAR(500),
    precio_por_persona  DECIMAL(10,2)  NOT NULL,
    duracion_dias       INT,
    activo              BIT(1)         NOT NULL DEFAULT b'1',
    created_at          DATETIME(6)    NOT NULL,
    updated_at          DATETIME(6)    NOT NULL,
    INDEX idx_paquete_destino (destino),
    INDEX idx_paquete_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS salida (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    paquete_id          BIGINT NOT NULL,
    fecha_salida        DATE   NOT NULL,
    fecha_regreso       DATE   NOT NULL,
    cupo_total          INT    NOT NULL,
    precio_por_persona  DECIMAL(10,2),
    camioneta_id        BIGINT,
    viaje_id            BIGINT,
    estado              ENUM('programada','cerrada','cancelada') NOT NULL DEFAULT 'programada',
    created_at          DATETIME(6) NOT NULL,
    updated_at          DATETIME(6) NOT NULL,
    CONSTRAINT fk_salida_paquete   FOREIGN KEY (paquete_id)   REFERENCES paquete(id),
    CONSTRAINT fk_salida_camioneta FOREIGN KEY (camioneta_id) REFERENCES camioneta(id),
    CONSTRAINT fk_salida_viaje     FOREIGN KEY (viaje_id)     REFERENCES viaje(id),
    INDEX idx_salida_fecha (estado, fecha_salida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reserva (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    salida_id                 BIGINT NOT NULL,
    usuario_publico_id        BIGINT NOT NULL,
    num_asientos              INT    NOT NULL,
    monto_total               DECIMAL(10,2) NOT NULL,
    monto_anticipo            DECIMAL(10,2) NOT NULL DEFAULT 0,
    monto_pagado              DECIMAL(10,2) NOT NULL DEFAULT 0,
    estado                    ENUM('pendiente_pago','en_revision','confirmada','cancelada') NOT NULL DEFAULT 'pendiente_pago',
    comprobante_url           VARCHAR(500),
    referencia_transferencia  VARCHAR(120),
    notas                     VARCHAR(500),
    confirmada_at             DATETIME(6),
    created_at                DATETIME(6) NOT NULL,
    updated_at                DATETIME(6) NOT NULL,
    CONSTRAINT fk_reserva_salida  FOREIGN KEY (salida_id)          REFERENCES salida(id),
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_publico_id) REFERENCES usuario_publico(id),
    INDEX idx_reserva_salida_estado (salida_id, estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorito (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_publico_id  BIGINT NOT NULL,
    paquete_id          BIGINT NOT NULL,
    created_at          DATETIME(6) NOT NULL,
    CONSTRAINT uk_favorito UNIQUE (usuario_publico_id, paquete_id),
    CONSTRAINT fk_fav_usuario FOREIGN KEY (usuario_publico_id) REFERENCES usuario_publico(id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_paquete FOREIGN KEY (paquete_id)         REFERENCES paquete(id)         ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
