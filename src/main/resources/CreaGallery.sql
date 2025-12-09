/*
Script de creacion de la base de datos para la tienda de zapatos Gallery Brands
Version simplificada - SIN tabla de tallas separada
Incluye datos de prueba para autenticación
CONTRASEÑA PARA TODOS LOS USUARIOS: "123"
*/

-- Para asegurarse que se creara solo una vez el base de datos y usuarios
DROP DATABASE IF EXISTS galleryB;
DROP USER IF EXISTS usuario_admin;
DROP USER IF EXISTS usuario_reportes;

-- Creacion del esquema
CREATE DATABASE galleryB
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
  
-- Creacion de usuarios con contraseñas seguras 
CREATE USER 'usuario_admin'@'%' IDENTIFIED BY 'Usuar1o_Admin.';
CREATE USER 'usuario_reportes'@'%' IDENTIFIED BY 'Usuar1o_Reportes.';

-- Asignacion de permisos a los usuarios
GRANT SELECT, INSERT, UPDATE, DELETE ON galleryB.* TO 'usuario_admin'@'%';
GRANT SELECT ON galleryB.* TO 'usuario_reportes'@'%';
FLUSH PRIVILEGES;

-- Seccion de Creacion de tablas
USE galleryB;

-- Tabla categoria de zapato
CREATE TABLE categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre_categoria VARCHAR(50) NOT NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (id_categoria),
  UNIQUE (nombre_categoria),
  INDEX ndx_nombre_categoria (nombre_categoria)
)
ENGINE = InnoDB;

-- Tabla Marca Zapato
CREATE TABLE marca (
  id_marca INT NOT NULL AUTO_INCREMENT,
  nombre_marca VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY (id_marca)
)
ENGINE = InnoDB;

-- Tabla Zapato - VERSIÓN SIMPLIFICADA
CREATE TABLE zapato (
  id_zapato INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  id_marca INT NOT NULL,
  nombre_zapato VARCHAR(50) NOT NULL,
  descripcion TEXT,
  precio DECIMAL(12,2) CHECK (precio >= 0),
  existencias INT UNSIGNED DEFAULT 0 CHECK (existencias >= 0),
  talla VARCHAR(50) NOT NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_zapato),
  UNIQUE (nombre_zapato),
  INDEX ndx_nombre_zapato (nombre_zapato),
  INDEX idx_zapato_marca (id_marca),
  FOREIGN KEY fk_producto_categoria (id_categoria) REFERENCES categoria(id_categoria),
  FOREIGN KEY fk_zapato_marca (id_marca) REFERENCES marca (id_marca)
)
ENGINE = InnoDB;

-- Tabla usuarios
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  password VARCHAR(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  INDEX ndx_username (username)
)
ENGINE = InnoDB;

-- Tabla reseña
CREATE TABLE resena_zapato (
  id_resena INT NOT NULL AUTO_INCREMENT,
  id_zapato INT NOT NULL,
  id_usuario INT NOT NULL,
  comentario TEXT,
  estrellas TINYINT CHECK (estrellas BETWEEN 1 AND 5),
  ruta_imagen VARCHAR(1024),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_resena),
  INDEX idx_zapato_resena (id_zapato),
  FOREIGN KEY fk_resena_zapato (id_zapato) REFERENCES zapato (id_zapato),
  FOREIGN KEY fk_resena_usuario (id_usuario) REFERENCES usuario (id_usuario)
)
ENGINE = InnoDB;

-- Tabla de facturas
CREATE TABLE facturas (
  id_factura INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  fecha DATE,
  total DECIMAL(12,2) CHECK (total > 0),
  estado ENUM('Activa', 'Pagada','Anulada') NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_factura),
  INDEX ndx_id_usuario (id_usuario),
  FOREIGN KEY fk_factura_usuario (id_usuario) REFERENCES usuario (id_usuario)
)
ENGINE = InnoDB;

-- Tabla de ventas (SIMPLIFICADA - sin talla)
CREATE TABLE venta (
  id_venta INT NOT NULL AUTO_INCREMENT,
  id_factura INT NOT NULL,
  id_zapato INT NOT NULL,
  precio_historico DECIMAL(12,2) CHECK (precio_historico >= 0),
  cantidad INT UNSIGNED CHECK (cantidad > 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_venta),
  INDEX ndx_factura (id_factura),
  INDEX ndx_zapato (id_zapato),
  UNIQUE (id_factura, id_zapato),
  FOREIGN KEY fk_venta_factura (id_factura) REFERENCES facturas(id_factura),
  FOREIGN KEY fk_venta_zapato (id_zapato) REFERENCES zapato(id_zapato)
)
ENGINE = InnoDB;

-- Cabecera del carrito 
CREATE TABLE carrito (
  id_carrito INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carrito),
  FOREIGN KEY fk_carrito_usuario (id_usuario) REFERENCES usuario (id_usuario)
)
ENGINE=InnoDB;

-- Detalle carrito (SIMPLIFICADO - sin talla)
CREATE TABLE carrito_detalle (
  id_carrito INT NOT NULL,
  id_zapato INT NOT NULL,
  cantidad INT UNSIGNED CHECK (cantidad > 0),
  precio_unitario DECIMAL(12,2) CHECK (precio_unitario >= 0),
  PRIMARY KEY (id_carrito, id_zapato),
  FOREIGN KEY fk_carrito (id_carrito) REFERENCES carrito (id_carrito),
  FOREIGN KEY fk_carrito_zapato (id_zapato) REFERENCES zapato (id_zapato)
)
ENGINE=InnoDB;

-- Tabla de roles
CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol VARCHAR(20) UNIQUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rol)
)
ENGINE = InnoDB;
  
-- Tabla de relación entre usuarios y roles
CREATE TABLE usuario_rol (
  id_usuario INT NOT NULL,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_rol),
  FOREIGN KEY fk_usuarioRol_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_usuarioRol_rol (id_rol) REFERENCES rol(id_rol)
)
ENGINE = InnoDB;

-- Tabla de rutas
CREATE TABLE ruta (
  id_ruta INT AUTO_INCREMENT NOT NULL,
  ruta VARCHAR(255) NOT NULL,
  id_rol INT NULL,
  requiere_rol BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (id_rol IS NOT NULL OR requiere_rol = FALSE),
  PRIMARY KEY (id_ruta),
  FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
)
ENGINE = InnoDB;

-- Tabla de constantes de la aplicación
CREATE TABLE constante (
  id_constante INT AUTO_INCREMENT NOT NULL,
  atributo VARCHAR(25) NOT NULL,
  valor VARCHAR(150) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_constante),
  UNIQUE (atributo)
)
ENGINE = InnoDB;

/*
========================================
POBLAR LAS TABLAS CON DATOS DE PRUEBA
========================================
*/

-- Poblar tabla categoria
INSERT INTO categoria (nombre_categoria, ruta_imagen, activo)
VALUES 
('Formal', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrE57O1gq8afzC8dGLZLrWBAPjzVyVsrzf3w&s', TRUE),
('Deportiva', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5lCSWFEPwu0w257JgTz9iYpjMUOSZ-PcncA&s', TRUE),
('Sneakers', 'https://m.media-amazon.com/images/I/81knDNjiLhL.jpg', TRUE);

-- Poblar tabla marca
INSERT INTO marca (nombre_marca)
VALUES 
('Sperry'),
('Nike'),
('Adidas');

-- Poblar tabla zapato (VERSIÓN SIMPLIFICADA)
INSERT INTO zapato (id_categoria, id_marca, nombre_zapato, descripcion, precio, existencias, talla, ruta_imagen, activo)
VALUES 
(1, 1, 'Authentic Original', 'Zapato formal cómodo y elegante', 50000.00, 25, 'S',
 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTu8RIT5lQnBZfuyEnRllU-KiXx35nqPpfkdQ&s', TRUE),
(2, 2, 'Nike Air Max', 'Zapatilla deportiva con tecnología Air', 85000.00, 30, 'M',
 'https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/99486859-0ff3-46b4-949b-2d16af2ad421/calzado-air-max-90-MwCLJK.png', TRUE),
(3, 3, 'Adidas Superstar', 'Sneaker clásico icónico', 65000.00, 20, 'L',
 'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/3bbecbdf584e40398446a8bf0117cf62_9366/Tenis_Superstar_Blanco_EG4958_01_standard.jpg', TRUE);

-- ====================================
-- POBLAR TABLA ROL (3 roles básicos)
-- ====================================
INSERT INTO rol (rol) 
VALUES 
('ROLE_ADMIN'),
('ROLE_VENDEDOR'),
('ROLE_USUARIO');

-- ============================================================================
-- POBLAR TABLA USUARIO
-- CONTRASEÑA PARA TODOS: "123"
-- Hash BCrypt CORRECTO: $2a$10$N9qo8uLOickgx2ZMRZoMye/IFdJHqNq7b2PWMq7XHqKjJlqfXp0qO
-- ============================================================================
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, ruta_imagen, activo)
VALUES 
('juan', '$2a$10$yItQW65HSi6hWP11dHxv2OWGyTf3q5YHPqki/KpvWoDoOSKzbSb.u', 
 'Juan', 'Pérez', 'juan@mail.com', '88881234', 
 'https://st.depositphotos.com/1144472/2003/i/950/depositphotos_20030237-stock-photo-cheerful-young-man-over-white.jpg', TRUE),
 
('rebeca', '$2a$10$yItQW65HSi6hWP11dHxv2OWGyTf3q5YHPqki/KpvWoDoOSKzbSb.u', 
 'Rebeca', 'González', 'rebeca@mail.com', '88881235', 
 'https://thumbs.dreamstime.com/b/smiling-business-woman-isolated-over-white-background-mature-49170992.jpg', TRUE),
 
('pedro', '$2a$10$yItQW65HSi6hWP11dHxv2OWGyTf3q5YHPqki/KpvWoDoOSKzbSb.u', 
 'Pedro', 'Martínez', 'pedro@mail.com', '88881236', 
 'https://t4.ftcdn.net/jpg/02/98/28/89/360_F_298288984_8i0PB7s9aWPzi1LeuNGGrnjXkmXRpcZn.jpg', TRUE),
 
('admin', '$2a$10$yItQW65HSi6hWP11dHxv2OWGyTf3q5YHPqki/KpvWoDoOSKzbSb.u', 
 'Alfredo', 'Valenzuela', 'alfredo@mail.com', '88881237', 
 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIt9pSWLfyT326xneRNhKBU3CHn4zVktQI0w&s', TRUE);

-- ======================================
-- ASIGNAR ROLES A LOS USUARIOS
-- juan = ADMIN
-- rebeca = VENDEDOR  
-- pedro = USUARIO
-- admin = ADMIN
-- ======================================
INSERT INTO usuario_rol (id_usuario, id_rol)
VALUES 
(1, 1), -- juan es ROLE_ADMIN
(2, 2), -- rebeca es ROLE_VENDEDOR
(3, 3), -- pedro es ROLE_USUARIO
(4, 1); -- admin es ROLE_ADMIN

-- Poblar ruta
-- Rutas de admin 
INSERT INTO ruta (ruta, id_rol, requiere_rol) 
VALUES 
('/dashboard', 1, TRUE),
('/zapato/nuevo', 1, TRUE),
('/zapato/modificar/**', 1, TRUE),
('/zapato/eliminar/**', 1, TRUE),
('/zapato/actualizar-existencias', 1, TRUE),
('/categoria/listado',       1, TRUE),
('/categoria/nuevo',         1, TRUE),
('/categoria/guardar',       1, TRUE),
('/categoria/modificar/**',  1, TRUE),
('/categoria/eliminar/**',   1, TRUE),
('/marca/listado',        1, TRUE),
('/marca/nuevo',          1, TRUE),
('/marca/guardar',        1, TRUE),
('/marca/modificar/**',   1, TRUE),
('/marca/eliminar/**',    1, TRUE),
('/', NULL, FALSE);

-- Rutas publicas 

INSERT INTO ruta (ruta, requiere_rol) VALUES
('/',                FALSE),
('/Inicio',          FALSE),
('/login',           FALSE),
('/sobre',           FALSE),
('/terminos',        FALSE),
('/privacidad',      FALSE),

-- Zapatos públicos
('/zapato/listado',  FALSE),
('/zapato/detalle/**', FALSE),
('/zapato/talla/**', FALSE),

-- Carrito público (Spring normalmente permite ver pero no facturar)
('/carrito/listado', FALSE),
('/carrito/agregar', FALSE),
('/carrito/limpiar', FALSE),
('/carrito/eliminar/**', FALSE),
('/carrito/modificar/**', FALSE),
('/carrito/actualizar', FALSE),

-- Recursos estáticos
('/css/**',     FALSE),
('/js/**',      FALSE),
('/images/**',  FALSE),
('/webjars/**', FALSE);


-- Poblar constante
INSERT INTO constante (atributo, valor) 
VALUES 
('moneda', 'CRC'),
('iva', '13');

-- ========================================
-- VERIFICACIÓN FINAL DE DATOS
-- ========================================
SELECT '==============================================';
SELECT 'VERIFICACIÓN DE USUARIOS Y ROLES' AS INFO;
SELECT '==============================================';

SELECT 
    u.id_usuario,
    u.username, 
    u.nombre, 
    u.apellidos, 
    u.activo,
    r.rol,
    LEFT(u.password, 30) AS password_hash_inicio
FROM usuario u
LEFT JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario
LEFT JOIN rol r ON ur.id_rol = r.id_rol
ORDER BY u.id_usuario;

SELECT '==============================================';
SELECT 'CREDENCIALES DE PRUEBA:' AS INFO;
SELECT 'Username: juan     | Password: 123 | Rol: ADMIN' AS CREDENCIALES
UNION ALL
SELECT 'Username: rebeca   | Password: 123 | Rol: VENDEDOR'
UNION ALL
SELECT 'Username: pedro    | Password: 123 | Rol: USUARIO'
UNION ALL
SELECT 'Username: admin    | Password: 123 | Rol: ADMIN';
SELECT '==============================================';



