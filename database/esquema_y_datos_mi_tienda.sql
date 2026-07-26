-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-07-2026 a las 01:49:42
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `mi_tienda`
--

CREATE DATABASE IF NOT EXISTS mi_tienda;
USE mi_tienda;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `configuraciones_sistema`
--

CREATE TABLE `configuraciones_sistema` (
  `clave` varchar(50) NOT NULL,
  `valor` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `configuraciones_sistema`
--

INSERT INTO `configuraciones_sistema` (`clave`, `valor`, `descripcion`) VALUES
('NombreProyectoPropioOriginal', 'SLIM', 'Nombre de la Tienda del Proyecto Original de PSeint con el que me motive a Aprender Java y Empezar este Proyecto');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `descuentos`
--

CREATE TABLE `descuentos` (
  `id_descuento` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `porcentaje` decimal(5,2) NOT NULL,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `descuentos`
--

INSERT INTO `descuentos` (`id_descuento`, `nombre`, `porcentaje`, `activo`) VALUES
(1, 'Sin Descuento', 0.00, 1),
(4, 'Descuento Navidad', 20.00, 1),
(5, 'Descuento Halloween', 25.00, 1),
(7, 'prueba', 12.00, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_facturas`
--

CREATE TABLE `detalle_facturas` (
  `id_detalle` int(11) NOT NULL,
  `id_factura` int(11) NOT NULL,
  `tipo_item` varchar(10) NOT NULL,
  `codigo_referencia` varchar(50) NOT NULL,
  `nombre_item` varchar(150) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal_neto` decimal(10,2) NOT NULL,
  `porcentaje_impuesto` decimal(5,2) NOT NULL,
  `monto_impuesto` decimal(10,2) NOT NULL,
  `total_linea` decimal(10,2) NOT NULL
) ;

--
-- Volcado de datos para la tabla `detalle_facturas`
--

INSERT INTO `detalle_facturas` (`id_detalle`, `id_factura`, `tipo_item`, `codigo_referencia`, `nombre_item`, `cantidad`, `precio_unitario`, `subtotal_neto`, `porcentaje_impuesto`, `monto_impuesto`, `total_linea`) VALUES
(6, 1, 'PRODUCTO', '1f60ae36-f66f-411f-b3ac-7e4d7950be76', 'Jean Azul Oscuro M', 1, 92820.00, 78000.00, 19.00, 14820.00, 92820.00),
(7, 2, 'PRODUCTO', '49aec878-5f9c-4138-8ddc-5b9e569f785d', 'Camisa Azul S', 5, 64260.00, 270000.00, 19.00, 51300.00, 321300.00),
(8, 3, 'SERVICIO', 'c6706e81-a32b-49c8-beaa-f492cba36ac4', 'Domicilio', 1, 15000.00, 15000.00, 0.00, 0.00, 15000.00),
(9, 3, 'PRODUCTO', '1f60ae36-f66f-411f-b3ac-7e4d7950be76', 'Jean Azul Oscuro M', 1, 116025.00, 97500.00, 19.00, 18525.00, 116025.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `facturas`
--

CREATE TABLE `facturas` (
  `id_factura` int(11) NOT NULL,
  `numero_factura` varchar(20) NOT NULL,
  `fecha` datetime NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `total_impuestos` decimal(10,2) NOT NULL,
  `total_general` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `facturas`
--

INSERT INTO `facturas` (`id_factura`, `numero_factura`, `fecha`, `subtotal`, `total_impuestos`, `total_general`) VALUES
(1, 'FAC-00001', '2026-07-10 14:05:19', 78000.00, 14820.00, 92820.00),
(2, 'FAC-00002', '2026-07-11 11:44:34', 270000.00, 51300.00, 321300.00),
(3, 'FAC-00003', '2026-07-13 12:13:44', 112500.00, 18525.00, 131025.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `impuestos`
--

CREATE TABLE `impuestos` (
  `id_impuesto` int(11) NOT NULL,
  `nombre` varchar(20) NOT NULL,
  `porcentaje` decimal(5,2) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `impuestos`
--

INSERT INTO `impuestos` (`id_impuesto`, `nombre`, `porcentaje`, `activo`) VALUES
(1, 'IVA General 2026', 19.00, 1),
(2, 'IVA Reducido 2026', 5.00, 1),
(3, 'Exento de Impuesto', 0.00, 1),
(7, 'Prueba', 12.00, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inventarios`
--

CREATE TABLE `inventarios` (
  `id_inventario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `capacidad_maxima` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `inventarios`
--

INSERT INTO `inventarios` (`id_inventario`, `nombre`, `capacidad_maxima`) VALUES
(2, 'Bodega Principal', 650),
(3, 'Bodega Local 1', 500),
(4, 'Bodega Local 2', 350),
(6, 'prueba', 1000),
(7, 'prueba 2.0', 9);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `politicas_vencimiento`
--

CREATE TABLE `politicas_vencimiento` (
  `id_politica` int(11) NOT NULL,
  `nombre_politica` varchar(100) NOT NULL,
  `dias_umbral` int(11) NOT NULL,
  `porcentaje_descuento` decimal(5,2) NOT NULL,
  `activa` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `politicas_vencimiento`
--

INSERT INTO `politicas_vencimiento` (`id_politica`, `nombre_politica`, `dias_umbral`, `porcentaje_descuento`, `activa`) VALUES
(1, 'Sin política de vencimiento', 0, 0.00, 1),
(2, 'Política General', 3, 30.00, 1),
(5, 'Prueba 1', 4, 20.00, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `codigo_producto` varchar(50) NOT NULL,
  `id_inventario` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `valor_compra` decimal(10,2) NOT NULL,
  `porcentaje_ganancia` decimal(5,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `id_impuesto` int(11) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `id_descuento` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`codigo_producto`, `id_inventario`, `nombre`, `valor_compra`, `porcentaje_ganancia`, `stock`, `id_impuesto`, `activo`, `id_descuento`) VALUES
('1f60ae36-f66f-411f-b3ac-7e4d7950be76', 2, 'Jean Azul Oscuro M', 65000.00, 60.00, 19, 1, 1, 1),
('3251602d-43b0-4add-9e92-0b744011cd78', 2, 'Camisa Verde M', 45000.00, 20.00, 20, 1, 1, 1),
('441235bf-f54d-4008-8f46-e612c5dcd841', 2, 'Pantaloneta Negra M', 35000.00, 20.00, 20, 1, 1, 1),
('49aec878-5f9c-4138-8ddc-5b9e569f785d', 2, 'Camisa Azul S', 45000.00, 20.00, 15, 1, 1, 1),
('57ba6551-695a-44ca-9907-5c31108a2283', 7, 'fhdfg', 123.00, 12.00, 0, 3, 1, 1),
('68759028-4b05-4547-8c78-cf4f7b484e84', 6, 'asdfgh', 123.00, 23.00, 12, 3, 1, 1),
('7961030a-ecd1-43b1-a757-06b751e30887', 2, 'Yogurt Griego 500g', 2500.00, 20.00, 25, 2, 1, 1),
('a0899808-8c2b-4cdc-bce4-081aeef226a6', 2, 'DeTodito Picante 250g', 2000.00, 20.00, 25, 2, 1, 1),
('b054852e-96f9-44fc-aa0f-a8f441369066', 6, 'bnm', 100.00, 20.00, 1, 3, 1, 1),
('b8729e2e-287b-4b32-bca2-d9c4a344a2a4', 2, 'Jean Azul Claro M', 65000.00, 20.00, 20, 1, 1, 1),
('c0cbec92-9009-4c88-8ac8-59ecb24b1206', 6, 'prueba', 1000.00, 50.00, 10, 3, 1, 1),
('cdb20b21-cf92-4bef-a61f-0eeb8eb305d5', 2, 'Galletas Oreo x4', 500.00, 20.00, 25, 2, 1, 1),
('deda11a0-cf75-4432-ac84-8004b67af806', 3, 'Leche Entera 1L', 2000.00, 100.00, 0, 2, 0, 1),
('e073abf8-2572-41cb-83f3-8bc162f72615', 2, 'Galletas Oreo x6', 750.00, 20.00, 25, 2, 1, 1),
('e1573014-b57f-4730-b693-c22cda9835ca', 7, 'asdas', 123213.00, 12.00, 9, 1, 1, 1),
('e31c1fae-c1ab-4eea-86bc-04d2cf6a0555', 6, 'Prueba comida asd', 1000.00, 90.00, 100, 2, 1, 1),
('e7502f29-a537-4aa4-ba9b-c0724ec3510f', 2, 'Cheese Tris', 1000.00, 20.00, 20, 2, 1, 1),
('f10dbec7-4251-413f-9d1c-c6405511191e', 2, 'Pack x3 de Medias Negras', 7500.00, 20.00, 20, 1, 1, 1),
('fa4a1713-8de9-4720-8b2a-936e59cefcaf', 2, 'Chaqueta Negra M', 75000.00, 20.00, 20, 1, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto_perecedero`
--

CREATE TABLE `producto_perecedero` (
  `codigo_producto` varchar(50) NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `id_politica` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `producto_perecedero`
--

INSERT INTO `producto_perecedero` (`codigo_producto`, `fecha_vencimiento`, `id_politica`) VALUES
('68759028-4b05-4547-8c78-cf4f7b484e84', '2026-07-23', 2),
('7961030a-ecd1-43b1-a757-06b751e30887', '2027-11-17', 2),
('a0899808-8c2b-4cdc-bce4-081aeef226a6', '2028-09-24', 2),
('cdb20b21-cf92-4bef-a61f-0eeb8eb305d5', '2028-02-09', 2),
('deda11a0-cf75-4432-ac84-8004b67af806', '2027-08-08', 2),
('e073abf8-2572-41cb-83f3-8bc162f72615', '2028-04-11', 2),
('e31c1fae-c1ab-4eea-86bc-04d2cf6a0555', '2028-07-06', 2),
('e7502f29-a537-4aa4-ba9b-c0724ec3510f', '2027-03-08', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto_ropa`
--

CREATE TABLE `producto_ropa` (
  `codigo_producto` varchar(50) NOT NULL,
  `talla` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `producto_ropa`
--

INSERT INTO `producto_ropa` (`codigo_producto`, `talla`) VALUES
('1f60ae36-f66f-411f-b3ac-7e4d7950be76', 'M'),
('3251602d-43b0-4add-9e92-0b744011cd78', 'M'),
('441235bf-f54d-4008-8f46-e612c5dcd841', 'M'),
('49aec878-5f9c-4138-8ddc-5b9e569f785d', 'S'),
('57ba6551-695a-44ca-9907-5c31108a2283', 'M'),
('b054852e-96f9-44fc-aa0f-a8f441369066', 'S'),
('b8729e2e-287b-4b32-bca2-d9c4a344a2a4', 'M'),
('c0cbec92-9009-4c88-8ac8-59ecb24b1206', 'M'),
('e1573014-b57f-4730-b693-c22cda9835ca', 'S'),
('f10dbec7-4251-413f-9d1c-c6405511191e', 'M'),
('fa4a1713-8de9-4720-8b2a-936e59cefcaf', 'M');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `secuencias_factura`
--

CREATE TABLE `secuencias_factura` (
  `prefijo` varchar(10) NOT NULL,
  `siguiente_valor` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `secuencias_factura`
--

INSERT INTO `secuencias_factura` (`prefijo`, `siguiente_valor`) VALUES
('FAC-', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicios`
--

CREATE TABLE `servicios` (
  `codigo_servicio` varchar(50) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `precio_base` decimal(10,2) DEFAULT NULL,
  `id_impuesto` int(11) DEFAULT NULL,
  `id_descuento` int(11) NOT NULL DEFAULT 1,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `servicios`
--

INSERT INTO `servicios` (`codigo_servicio`, `nombre`, `precio_base`, `id_impuesto`, `id_descuento`, `activo`) VALUES
('03dd903d-0069-497f-b009-38af72862997', 'pruebas a', 20000.00, 3, 1, 1),
('26db7c18-e52a-4628-b0ce-39e32c2219b3', 'Empaquetado Individual', 3000.00, 3, 1, 1),
('7c995605-03d0-4cf4-974a-749e04172255', 'Prueba', 12345.00, 1, 1, 1),
('ab2f7028-819e-41e5-85b2-4351e5348640', 'Empaquetado General', 25000.00, 3, 1, 1),
('c6706e81-a32b-49c8-beaa-f492cba36ac4', 'Domicilio', 15000.00, 3, 1, 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `configuraciones_sistema`
--
ALTER TABLE `configuraciones_sistema`
  ADD PRIMARY KEY (`clave`);

--
-- Indices de la tabla `descuentos`
--
ALTER TABLE `descuentos`
  ADD PRIMARY KEY (`id_descuento`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `detalle_facturas`
--
ALTER TABLE `detalle_facturas`
  ADD PRIMARY KEY (`id_detalle`),
  ADD KEY `fk_detalle_factura_cabecera` (`id_factura`);

--
-- Indices de la tabla `facturas`
--
ALTER TABLE `facturas`
  ADD PRIMARY KEY (`id_factura`),
  ADD UNIQUE KEY `numero_factura` (`numero_factura`);

--
-- Indices de la tabla `impuestos`
--
ALTER TABLE `impuestos`
  ADD PRIMARY KEY (`id_impuesto`),
  ADD UNIQUE KEY `uk_nombre_impuesto` (`nombre`);

--
-- Indices de la tabla `inventarios`
--
ALTER TABLE `inventarios`
  ADD PRIMARY KEY (`id_inventario`);

--
-- Indices de la tabla `politicas_vencimiento`
--
ALTER TABLE `politicas_vencimiento`
  ADD PRIMARY KEY (`id_politica`),
  ADD UNIQUE KEY `nombre_politica` (`nombre_politica`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`codigo_producto`),
  ADD KEY `productos_ibfk_1` (`id_inventario`),
  ADD KEY `fk_productos_impuestos` (`id_impuesto`),
  ADD KEY `fk_productos_descuento` (`id_descuento`);

--
-- Indices de la tabla `producto_perecedero`
--
ALTER TABLE `producto_perecedero`
  ADD PRIMARY KEY (`codigo_producto`),
  ADD KEY `fk_perecedero_politica` (`id_politica`);

--
-- Indices de la tabla `producto_ropa`
--
ALTER TABLE `producto_ropa`
  ADD PRIMARY KEY (`codigo_producto`);

--
-- Indices de la tabla `secuencias_factura`
--
ALTER TABLE `secuencias_factura`
  ADD PRIMARY KEY (`prefijo`);

--
-- Indices de la tabla `servicios`
--
ALTER TABLE `servicios`
  ADD PRIMARY KEY (`codigo_servicio`),
  ADD KEY `id_impuesto` (`id_impuesto`),
  ADD KEY `fk_servicios_descuento` (`id_descuento`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `descuentos`
--
ALTER TABLE `descuentos`
  MODIFY `id_descuento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `detalle_facturas`
--
ALTER TABLE `detalle_facturas`
  MODIFY `id_detalle` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `facturas`
--
ALTER TABLE `facturas`
  MODIFY `id_factura` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `impuestos`
--
ALTER TABLE `impuestos`
  MODIFY `id_impuesto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `inventarios`
--
ALTER TABLE `inventarios`
  MODIFY `id_inventario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `politicas_vencimiento`
--
ALTER TABLE `politicas_vencimiento`
  MODIFY `id_politica` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalle_facturas`
--
ALTER TABLE `detalle_facturas`
  ADD CONSTRAINT `fk_detalle_factura_cabecera` FOREIGN KEY (`id_factura`) REFERENCES `facturas` (`id_factura`) ON DELETE CASCADE;

--
-- Filtros para la tabla `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `fk_productos_descuento` FOREIGN KEY (`id_descuento`) REFERENCES `descuentos` (`id_descuento`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_productos_impuestos` FOREIGN KEY (`id_impuesto`) REFERENCES `impuestos` (`id_impuesto`) ON UPDATE CASCADE,
  ADD CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_inventario`) REFERENCES `inventarios` (`id_inventario`);

--
-- Filtros para la tabla `producto_perecedero`
--
ALTER TABLE `producto_perecedero`
  ADD CONSTRAINT `fk_perecedero_politica` FOREIGN KEY (`id_politica`) REFERENCES `politicas_vencimiento` (`id_politica`),
  ADD CONSTRAINT `producto_perecedero_ibfk_1` FOREIGN KEY (`codigo_producto`) REFERENCES `productos` (`codigo_producto`) ON DELETE CASCADE;

--
-- Filtros para la tabla `producto_ropa`
--
ALTER TABLE `producto_ropa`
  ADD CONSTRAINT `producto_ropa_ibfk_1` FOREIGN KEY (`codigo_producto`) REFERENCES `productos` (`codigo_producto`) ON DELETE CASCADE;

--
-- Filtros para la tabla `servicios`
--
ALTER TABLE `servicios`
  ADD CONSTRAINT `fk_servicios_descuento` FOREIGN KEY (`id_descuento`) REFERENCES `descuentos` (`id_descuento`) ON UPDATE CASCADE,
  ADD CONSTRAINT `servicios_ibfk_1` FOREIGN KEY (`id_impuesto`) REFERENCES `impuestos` (`id_impuesto`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
