/*
Script de creacion de la base de datos para la tienda de zapatos Gallery Brands
Version simplificada - SIN tabla de tallas separada
*/

-- Para asegurarse que se creara solo una vez el base de datos y usuarios
drop database if exists galleryB;
drop user if exists usuario_admin;
drop user if exists usuario_reportes;

-- Creacion del esquema
CREATE database galleryB
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
  
-- Creacion de usuarios con contraseñas seguras 
create user 'usuario_admin'@'%' identified by 'Usuar1o_Admin.';
create user 'usuario_reportes'@'%' identified by 'Usuar1o_Reportes.';

-- Asignacion de permisos a los usuarios
grant select, insert, update, delete on galleryB.* to 'usuario_admin'@'%';
grant select on galleryB.* to 'usuario_reportes'@'%';
flush privileges;

-- Seccion de Creacion de tablas

USE galleryB;

-- Tabla categoria de zapato
create table categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre_categoria VARCHAR(50) NOT NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (id_categoria),
  unique (nombre_categoria),
  INDEX ndx_nombre_categoria (nombre_categoria)
)
ENGINE = InnoDB;

-- Tabla Marca Zapato
create table marca (
  id_marca INT NOT NULL AUTO_INCREMENT,
  nombre_marca VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY (id_marca)
)
ENGINE = InnoDB;

-- Tabla Zapato - VERSIÓN SIMPLIFICADA
create table zapato (
  id_zapato INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  id_marca INT NOT NULL,
  nombre_zapato VARCHAR(50) NOT NULL,
  descripcion text,
  precio decimal(12,2) CHECK (precio >= 0),
  existencias int unsigned DEFAULT 0 CHECK (existencias >= 0),
  talla VARCHAR(50) NOT NULL,
  ruta_imagen varchar(1024),
  activo boolean DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_zapato),
  unique (nombre_zapato),
  INDEX ndx_nombre_zapato (nombre_zapato),
  INDEX idx_zapato_marca (id_marca),
  foreign key fk_producto_categoria (id_categoria) references categoria(id_categoria),
  foreign key fk_zapato_marca (id_marca) references marca (id_marca)
)
ENGINE = InnoDB;

-- Tabla usuarios
create table usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username varchar(30) NOT NULL UNIQUE,
  password varchar(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75)NULL UNIQUE,
  telefono VARCHAR(25)NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  INDEX ndx_username (username)
)
ENGINE = InnoDB;

-- Tabla reseña
create table resena_zapato (
  id_resena INT NOT NULL AUTO_INCREMENT,
  id_zapato INT NOT NULL,
  id_usuario INT NOT NULL,
  comentario text,
  estrellas TINYINT CHECK (estrellas BETWEEN 1 AND 5),
  ruta_imagen varchar(1024),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_resena),
  INDEX idx_zapato_resena (id_zapato),
  foreign key fk_resena_zapato (id_zapato) references zapato (id_zapato),
  foreign key fk_resena_usuario (id_usuario) references usuario (id_usuario)
)
ENGINE = InnoDB;

-- Tabla de facturas
create table facturas (
  id_factura INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  fecha date,
  total decimal (12,2) CHECK (total > 0),
  estado ENUM ('Activa', 'Pagada','Anulada') NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_factura),
  INDEX ndx_id_usuario (id_usuario),
  foreign key fk_factura_usuario (id_usuario) references usuario (id_usuario)
)
ENGINE = InnoDB;

-- Tabla de ventas (SIMPLIFICADA - sin talla)
create table venta (
  id_venta INT NOT NULL AUTO_INCREMENT,
  id_factura INT NOT NULL,
  id_zapato INT NOT NULL,
  precio_historico decimal(12,2) CHECK (precio_historico>= 0),
  cantidad int unsigned check (cantidad > 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_venta),
  INDEX ndx_factura (id_factura),
  INDEX ndx_zapato (id_zapato),
  UNIQUE (id_factura, id_zapato),  -- Un zapato solo puede aparecer una vez por factura
  foreign key fk_venta_factura (id_factura) references facturas(id_factura),
  foreign key fk_venta_zapato (id_zapato) references zapato(id_zapato)
)
ENGINE = InnoDB;

-- Cabecera del carrito 
create table carrito (
  id_carrito INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carrito),
  foreign key fk_carrito_usuario (id_usuario) references usuario (id_usuario)
)
ENGINE=InnoDB;

-- Detalle carrito (SIMPLIFICADO - sin talla)
create table carrito_detalle (
  id_carrito INT NOT NULL,
  id_zapato INT NOT NULL,
  cantidad INT unsigned CHECK (cantidad > 0),
  precio_unitario decimal(12,2) CHECK (precio_unitario >= 0),
  PRIMARY KEY (id_carrito,id_zapato),
  foreign key fk_carrito (id_carrito) references carrito (id_carrito),
  foreign key fk_carrito_zapato (id_zapato) references zapato (id_zapato)
)
ENGINE=InnoDB;

-- Tabla de roles
create table rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol varchar(20) unique,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  primary key (id_rol)
  )
ENGINE = InnoDB;
  
-- Tabla de relación entre usuarios y roles
create table usuario_rol (
  id_usuario int not null,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario,id_rol),
  foreign key fk_usuarioRol_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_usuarioRol_rol (id_rol) references rol(id_rol)
  )
ENGINE = InnoDB;

-- Tabla de rutas
CREATE TABLE ruta (
  id_ruta INT AUTO_INCREMENT NOT NULL,
  ruta VARCHAR(255) NOT NULL,
  id_rol INT NULL,
  requiere_rol boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  check (id_rol IS NOT NULL OR requiere_rol = FALSE),
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
Poblar las tablas con registros para tenerlos como referencia
*/

-- Poblar tabla categoria
INSERT INTO categoria (nombre_categoria, ruta_imagen, activo)
VALUES ('Formal', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrE57O1gq8afzC8dGLZLrWBAPjzVyVsrzf3w&s', TRUE),
('Deportiva', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5lCSWFEPwu0w257JgTz9iYpjMUOSZ-PcncA&s', TRUE),
('Sneakers', 'https://m.media-amazon.com/images/I/81knDNjiLhL.jpg', TRUE);

-- Poblar tabla marca
INSERT INTO marca (nombre_marca)
VALUES ('Sperry');

-- Poblar tabla zapato (VERSIÓN SIMPLIFICADA)
INSERT INTO zapato (id_categoria, id_marca, nombre_zapato, descripcion, precio, existencias,talla, ruta_imagen, activo)
VALUES 
(1, 1, 'Authentic Original', 'Zapato formal cómodo y elegante', 50000.00, 25, 'S',
 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTu8RIT5lQnBZfuyEnRllU-KiXx35nqPpfkdQ&s', TRUE);
 

-- Poblar Usuario
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, ruta_imagen, activo)
VALUES 
('admin', '$2a$10$r7vB1QY7q1S8d1J7q1S8d.1J7q1S8d1J7q1S8d1J7q1S8d1J7q1S8d', 'Alfredo', 'Valenzuela', 'alfredo@mail.com', '88881234', 
 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIt9pSWLfyT326xneRNhKBU3CHn4zVktQI0w&s', TRUE),
 
('cliente1', '$2a$10$r7vB1QY7q1S8d1J7q1S8d.1J7q1S8d1J7q1S8d1J7q1S8d1J7q1S8d', 'María', 'Rodríguez', 'maria@mail.com', '88881235', 
 NULL, TRUE);

-- Poblar Rol
INSERT INTO rol (rol) 
VALUES ('ROLE_ADMIN');

-- Poblar Usuario rol
INSERT INTO usuario_rol (id_usuario, id_rol)
VALUES (1, 1);

-- Poblar ruta
INSERT INTO ruta (ruta, id_rol, requiere_rol) 
VALUES ('/dashboard', 1, TRUE);

-- Poblar constante
INSERT INTO constante (atributo, valor) 
VALUES ('moneda', 'CRC');