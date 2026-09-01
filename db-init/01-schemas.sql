CREATE TYPE rol_enum AS ENUM ('ADMIN', 'RECEPCIONISTA');
CREATE TYPE estado_orden_enum AS ENUM ('INGRESADO', 'DIAGNOSTICO', 'EN_REPARACION', 'LISTO', 'ENTREGADO');
CREATE TYPE estado_enum AS ENUM ('ACTIVO', 'INACTIVO');

CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role rol_enum NOT NULL DEFAULT 'RECEPCIONISTA',
    estado estado_enum NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE repuestos (
    id_repuesto SERIAL PRIMARY KEY,
    codigo_referencia VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    proveedor VARCHAR(100) NOT NULL,
    stock_total INT NOT NULL CHECK (stock_total >= 0),
    stock_disponible INT NOT NULL CHECK (stock_disponible >= 0),
    precio_unitario NUMERIC(12, 2) NOT NULL CHECK (precio_unitario >= 0.00),
    is_activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_stock CHECK (stock_disponible <= stock_total)
);

CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    documento VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    estado estado_enum NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE vehiculos (
    id_vehiculo SERIAL PRIMARY KEY,
    placa VARCHAR(20) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    color VARCHAR(30) NOT NULL,
    id_cliente INT NOT NULL,
    CONSTRAINT fk_vehiculo_cliente 
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) 
        ON DELETE CASCADE
);

CREATE TABLE ordenes_servicio (
    id_orden SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_vehiculo INT NOT NULL,
    mecanico_responsable VARCHAR(100) NOT NULL,
    fecha_ingreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion_problema TEXT NOT NULL,
    diagnostico TEXT,
    estado_orden estado_orden_enum NOT NULL DEFAULT 'INGRESADO',
    costo_mano_obra NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (costo_mano_obra >= 0.00),
    CONSTRAINT fk_orden_cliente 
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    CONSTRAINT fk_orden_vehiculo 
        FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo)
);

CREATE TABLE ordenes_repuestos (
    id_orden INT NOT NULL,
    id_repuesto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_en_momento NUMERIC(12, 2) NOT NULL CHECK (precio_en_momento >= 0.00),
    PRIMARY KEY (id_orden, id_repuesto),
    CONSTRAINT fk_det_orden 
        FOREIGN KEY (id_orden) REFERENCES ordenes_servicio(id_orden) 
        ON DELETE CASCADE,
    CONSTRAINT fk_det_repuesto 
        FOREIGN KEY (id_repuesto) REFERENCES repuestos(id_repuesto)
);

INSERT INTO usuarios (username, password, role, estado)
VALUES ('Admin', '123', 'ADMIN', 'ACTIVO');

INSERT INTO usuarios (username, password, role, estado)
VALUES ('Secre', '123', 'RECEPCIONISTA', 'ACTIVO');