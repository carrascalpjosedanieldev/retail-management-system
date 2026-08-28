-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 28-08-2026 a las 01:47:54
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
-- Base de datos: `retail_management_system`
--

CREATE DATABASE IF NO EXISTS retail_management_system;
USE retail_management_system;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `configuraciones_sistema`
--

CREATE TABLE `configuraciones_sistema` (
  `clave` varchar(50) NOT NULL,
  `valor` varchar(100) NOT NULL,
  `descripcion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `configuraciones_sistema`
--

INSERT INTO `configuraciones_sistema` (`clave`, `valor`, `descripcion`) VALUES
('NOMBRE_PROYECTO_PROPIO_ORIGINAL', 'SLIM', 'Nombre de la Tienda del Proyecto Original de PSeint con el que me motive a Aprender Java y Empezar este Proyecto'),
('SEGURIDAD_MAX_INTENTOS', '5', 'Número máximo de intentos fallidos antes de bloquear la cuenta'),
('SEGURIDAD_MINUTOS_BLOQUEO', '15', 'Minutos que la cuenta se queda bloqueada por intentos fallidos');

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
(5, 'Descuento Halloween', 25.00, 1);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detalle_facturas`
--

INSERT INTO `detalle_facturas` (`id_detalle`, `id_factura`, `tipo_item`, `codigo_referencia`, `nombre_item`, `cantidad`, `precio_unitario`, `subtotal_neto`, `porcentaje_impuesto`, `monto_impuesto`, `total_linea`) VALUES
(6, 1, 'PRODUCTO', '1f60ae36-f66f-411f-b3ac-7e4d7950be76', 'Jean Azul Oscuro M', 1, 92820.00, 78000.00, 19.00, 14820.00, 92820.00),
(7, 2, 'PRODUCTO', '49aec878-5f9c-4138-8ddc-5b9e569f785d', 'Camisa Azul S', 5, 64260.00, 270000.00, 19.00, 51300.00, 321300.00),
(8, 3, 'SERVICIO', 'c6706e81-a32b-49c8-beaa-f492cba36ac4', 'Domicilio', 1, 15000.00, 15000.00, 0.00, 0.00, 15000.00),
(9, 3, 'PRODUCTO', '1f60ae36-f66f-411f-b3ac-7e4d7950be76', 'Jean Azul Oscuro M', 1, 116025.00, 97500.00, 19.00, 18525.00, 116025.00),
(10, 4, 'PRODUCTO', '1f60ae36-f66f-411f-b3ac-7e4d7950be76', 'Jean Azul Oscuro M', 2, 123760.00, 208000.00, 19.00, 39520.00, 247520.00),
(11, 4, 'SERVICIO', 'c6706e81-a32b-49c8-beaa-f492cba36ac4', 'Domicilio', 1, 15000.00, 15000.00, 0.00, 0.00, 15000.00),
(12, 5, 'PRODUCTO', '1f60ae36-f66f-411f-b3ac-7e4d7950be76', 'Jean Azul Oscuro M', 1, 123760.00, 104000.00, 19.00, 19760.00, 123760.00);

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
(3, 'FAC-00003', '2026-07-13 12:13:44', 112500.00, 18525.00, 131025.00),
(4, 'FAC-00004', '2026-08-12 18:35:29', 223000.00, 39520.00, 262520.00),
(5, 'FAC-00005', '2026-08-12 18:41:15', 104000.00, 19760.00, 123760.00);

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
(3, 'Exento de Impuesto', 0.00, 1);

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
(4, 'Bodega Local 2', 350);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `modulos`
--

CREATE TABLE `modulos` (
  `id_modulo` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `modulos`
--

INSERT INTO `modulos` (`id_modulo`, `nombre`, `descripcion`, `activo`) VALUES
(1, 'Ventas', 'Gestión de atención al cliente, emisión de facturas e historial de transacciones.', 1),
(2, 'Inventario', 'Administración de almacenes, catálogo de productos, existencias y traslados.', 1),
(3, 'Catálogo', 'Mantenimiento de los servicios ofrecidos por la empresa.', 1),
(4, 'Facturación y Ajustes', 'Configuración de parámetros comerciales como impuestos, descuentos y políticas de vencimiento.', 1),
(5, 'Configuración', 'Ajustes generales del sistema, incluyendo el perfil y nombre de la tienda.', 1),
(6, 'Seguridad', 'Control de accesos del sistema, gestión de roles y permisos de los usuarios.', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisos`
--

CREATE TABLE `permisos` (
  `id_permiso` int(10) UNSIGNED NOT NULL,
  `id_modulo` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  `actualizado_en` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `permisos`
--

INSERT INTO `permisos` (`id_permiso`, `id_modulo`, `nombre`, `descripcion`, `activo`, `creado_en`, `actualizado_en`) VALUES
(1, 1, 'Procesar Venta', 'Permite atender clientes, completar ventas y generar facturas.', 1, '2026-08-11 00:18:16', '2026-08-13 00:43:45'),
(2, 1, 'Ver Historial de Ventas', 'Permite filtrar por fechas y ver facturas emitidas y totales.', 1, '2026-08-11 00:18:16', '2026-08-11 00:18:16'),
(3, 2, 'Ver Inventarios', 'Permite listar los inventarios o almacenes registrados.', 1, '2026-08-11 00:18:16', '2026-08-11 00:18:16'),
(4, 2, 'Registrar Inventarios', 'Permite registrar inventarios en el sistema.', 1, '2026-08-11 00:18:16', '2026-08-27 21:57:09'),
(5, 2, 'Ver Productos', 'Permite consultar el catálogo de productos dentro de un inventario.', 1, '2026-08-11 00:18:16', '2026-08-11 00:18:16'),
(6, 2, 'Registrar Productos', 'Permite registrar productos en inventarios.', 1, '2026-08-11 00:18:16', '2026-08-27 22:33:33'),
(7, 2, 'Trasladar Productos', 'Permite mover existencias de mercancía entre diferentes inventarios.', 1, '2026-08-11 00:18:16', '2026-08-11 00:18:16'),
(8, 3, 'Ver Servicios', 'Permite consultar la lista de servicios ofrecidos.', 1, '2026-08-11 00:18:16', '2026-08-23 17:22:50'),
(9, 3, 'Registrar Servicios', 'Permite registrar servicios.', 1, '2026-08-11 00:18:16', '2026-08-26 19:25:20'),
(10, 4, 'Ver Impuestos', 'Permite ver los diferentes impuestos que hay en el sistema.', 1, '2026-08-11 00:18:16', '2026-08-26 18:53:11'),
(11, 4, 'Ver Descuentos', 'Permite ver los descuentos aplicables a las ventas.', 1, '2026-08-11 00:18:16', '2026-08-26 18:24:08'),
(12, 4, 'Ver Políticas V', 'Permite configurar las alertas y reglas para productos que caducan.', 1, '2026-08-11 00:18:16', '2026-08-26 19:09:54'),
(13, 5, 'Editar Perfil de Tienda', 'Permite cambiar el nombre, descripción y propósito del negocio.', 1, '2026-08-11 00:18:16', '2026-08-11 00:18:16'),
(14, 6, 'Editar Roles', 'Permite modificar roles del sistema.', 1, '2026-08-11 00:18:16', '2026-08-26 17:57:27'),
(15, 6, 'Gestionar Permisos', 'Permite activar y desactivar permisos para restringir o permitir la navegación en el sistema.', 1, '2026-08-11 00:18:16', '2026-08-26 17:41:59'),
(16, 6, 'Ver Usuarios', 'Permite visualizar los usuarios del sistema', 1, '2026-08-23 17:33:14', '2026-08-27 22:16:31'),
(17, 6, 'Ver Permisos', 'Permite ver los permisos para navegar en el sistema', 1, '2026-08-26 17:38:47', '2026-08-26 17:38:47'),
(18, 6, 'Ver Roles', 'Permite ver los roles que los usuarios pueden tener', 1, '2026-08-26 17:54:59', '2026-08-26 17:54:59'),
(19, 6, 'Registrar Roles', 'Permite registrar nuevos roles', 1, '2026-08-26 17:55:42', '2026-08-26 17:55:42'),
(20, 6, 'Administrar Permisos de Roles', 'Permite administrar los permisos que los roles pueden tener asignados', 1, '2026-08-26 17:56:38', '2026-08-26 17:56:38'),
(21, 4, 'Registrar Descuentos', 'Permite registrar descuentos aplicables a ventas', 1, '2026-08-26 18:26:47', '2026-08-26 18:26:47'),
(22, 4, 'Modificar Descuentos', 'Permite modificar descuentos aplicables a ventas', 1, '2026-08-26 18:26:47', '2026-08-26 18:26:47'),
(23, 4, 'Cambiar Estado Descuentos', 'Permite cambiar el estado de los descuentos', 1, '2026-08-26 18:26:47', '2026-08-26 18:26:47'),
(24, 4, 'Registrar Impuestos', 'Permite registrar impuestos en el sistema', 1, '2026-08-26 18:52:16', '2026-08-26 18:52:16'),
(25, 4, 'Modificar Impuestos', 'Permite modificar impuestos aplicables a ventas', 1, '2026-08-26 18:52:16', '2026-08-26 18:52:16'),
(26, 4, 'Cambiar Estado Impuestos', 'Permite cambiar el estado de los impuestos', 1, '2026-08-26 18:52:16', '2026-08-26 18:52:16'),
(28, 4, 'Registrar Políticas V', 'Permite registrar Políticas de Vencimiento', 1, '2026-08-26 19:11:48', '2026-08-26 19:11:48'),
(29, 4, 'Modificar Políticas V', 'Permite modificar las Políticas Vencimiento del sistema', 1, '2026-08-26 19:11:48', '2026-08-26 19:11:48'),
(30, 4, 'Cambiar Estado Políticas V', 'Permite cambiar el estado de las Políticas de Vencimiento', 1, '2026-08-26 19:11:48', '2026-08-26 19:11:48'),
(31, 3, 'Modificar Servicios', 'Permite modificar los servicios', 1, '2026-08-26 19:24:54', '2026-08-27 23:38:25'),
(32, 3, 'Cambiar Estado Servicios', 'Permite cambiar el estado de los servicios', 1, '2026-08-26 19:24:54', '2026-08-27 23:38:33'),
(33, 6, 'Registrar Usuarios', 'Permite registrar usuarios', 1, '2026-08-26 19:41:07', '2026-08-27 23:32:18'),
(34, 6, 'Editar Usuarios', 'Permite editar los usuarios', 1, '2026-08-26 19:41:07', '2026-08-27 23:32:00'),
(35, 6, 'Cambiar Estado Usuarios', 'Permite cambiar el estado de los usuarios', 1, '2026-08-26 19:41:07', '2026-08-27 23:32:37'),
(36, 6, 'Gestionar Roles del Usuario', 'Permite gestionar los roles del usuario', 1, '2026-08-26 19:41:07', '2026-08-27 23:32:56'),
(37, 6, 'Restablecer Contraseña Usuario', 'Permite restablecer la contraseña del usuario', 1, '2026-08-26 19:41:07', '2026-08-27 23:33:05'),
(38, 2, 'Editar Inventarios', 'Permite editar datos de los inventarios del sistema', 1, '2026-08-27 21:55:12', '2026-08-27 21:55:12'),
(39, 2, 'Editar Producto', 'Permite editar datos de los productos del sistema', 1, '2026-08-27 22:36:18', '2026-08-27 22:36:18'),
(40, 2, 'Manejar Stock Producto', 'Permite manejar el stock de los productos de los inventarios del sistema', 1, '2026-08-27 22:36:18', '2026-08-27 22:36:18'),
(41, 2, 'Cambiar Estado Producto', 'Permite activar y desactivar los productos del sistema', 1, '2026-08-27 22:36:18', '2026-08-27 22:36:18');

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
(2, 'Política General', 3, 30.00, 1);

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
('1f60ae36-f66f-411f-b3ac-7e4d7950be76', 2, 'Jean Azul Oscuro M', 65000.00, 60.00, 16, 1, 1, 1),
('3251602d-43b0-4add-9e92-0b744011cd78', 2, 'Camisa Verde M', 45000.00, 20.00, 20, 1, 1, 1),
('441235bf-f54d-4008-8f46-e612c5dcd841', 2, 'Pantaloneta Negra M', 35000.00, 20.00, 20, 1, 1, 1),
('49aec878-5f9c-4138-8ddc-5b9e569f785d', 2, 'Camisa Azul S', 45000.00, 75.00, 15, 1, 1, 1),
('7961030a-ecd1-43b1-a757-06b751e30887', 2, 'Yogurt Griego 500g', 2500.00, 20.00, 25, 2, 1, 1),
('a0899808-8c2b-4cdc-bce4-081aeef226a6', 2, 'DeTodito Picante 250g', 2000.00, 100.00, 25, 2, 1, 1),
('b8729e2e-287b-4b32-bca2-d9c4a344a2a4', 2, 'Jean Azul Claro M', 65000.00, 20.00, 30, 1, 1, 1),
('cdb20b21-cf92-4bef-a61f-0eeb8eb305d5', 2, 'Galletas Oreo x4', 500.00, 20.00, 25, 2, 1, 1),
('deda11a0-cf75-4432-ac84-8004b67af806', 3, 'Leche Entera 1L', 2000.00, 100.00, 0, 2, 1, 1),
('e073abf8-2572-41cb-83f3-8bc162f72615', 2, 'Galletas Oreo x6', 750.00, 20.00, 25, 2, 1, 1),
('e7502f29-a537-4aa4-ba9b-c0724ec3510f', 2, 'Cheese Tris', 1000.00, 20.00, 20, 2, 1, 1),
('f10dbec7-4251-413f-9d1c-c6405511191e', 2, 'Pack x3 de Medias Negras', 7500.00, 20.00, 20, 1, 1, 1),
('fa4a1713-8de9-4720-8b2a-936e59cefcaf', 2, 'Chaqueta Negra M', 75000.00, 80.00, 20, 1, 1, 1);

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
('7961030a-ecd1-43b1-a757-06b751e30887', '2027-11-17', 2),
('a0899808-8c2b-4cdc-bce4-081aeef226a6', '2028-09-24', 2),
('cdb20b21-cf92-4bef-a61f-0eeb8eb305d5', '2028-02-09', 2),
('deda11a0-cf75-4432-ac84-8004b67af806', '2027-08-08', 2),
('e073abf8-2572-41cb-83f3-8bc162f72615', '2028-04-11', 2),
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
('b8729e2e-287b-4b32-bca2-d9c4a344a2a4', 'M'),
('f10dbec7-4251-413f-9d1c-c6405511191e', 'M'),
('fa4a1713-8de9-4720-8b2a-936e59cefcaf', 'M');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id_rol` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  `actualizado_en` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id_rol`, `nombre`, `activo`, `creado_en`, `actualizado_en`) VALUES
(1, 'ADMINISTRADOR', 1, '2026-08-13 20:05:57', '2026-08-23 17:39:24'),
(2, 'CAJERO', 1, '2026-08-13 20:09:36', '2026-08-23 17:39:38'),
(3, 'JEFE DE ALMACEN', 1, '2026-08-13 20:56:28', '2026-08-23 17:39:58'),
(4, 'GERENTE COMERCIAL', 1, '2026-08-23 17:41:22', '2026-08-23 17:41:22'),
(5, 'ADMINISTRADOR DE SISTEMAS', 1, '2026-08-23 17:42:20', '2026-08-23 17:42:20');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol_permiso`
--

CREATE TABLE `rol_permiso` (
  `id_rol` int(10) UNSIGNED NOT NULL,
  `id_permiso` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rol_permiso`
--

INSERT INTO `rol_permiso` (`id_rol`, `id_permiso`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 17),
(1, 18),
(1, 19),
(1, 20),
(1, 21),
(1, 22),
(1, 23),
(1, 24),
(1, 25),
(1, 26),
(1, 28),
(1, 29),
(1, 30),
(1, 31),
(1, 32),
(1, 33),
(1, 34),
(1, 35),
(1, 36),
(1, 37),
(1, 38),
(1, 39),
(1, 40),
(1, 41),
(2, 1),
(2, 2),
(2, 3),
(2, 5),
(2, 8),
(2, 11),
(2, 12),
(3, 3),
(3, 4),
(3, 5),
(3, 6),
(3, 7),
(3, 12),
(3, 28),
(3, 29),
(3, 30),
(4, 2),
(4, 8),
(4, 9),
(4, 10),
(4, 11),
(4, 21),
(4, 22),
(4, 23),
(4, 24),
(4, 25),
(4, 26),
(4, 31),
(4, 32),
(5, 13),
(5, 14),
(5, 15),
(5, 16);

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
('FAC-', 6);

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
('26db7c18-e52a-4628-b0ce-39e32c2219b3', 'Empaquetado Individual', 3000.00, 3, 1, 1),
('ab2f7028-819e-41e5-85b2-4351e5348640', 'Empaquetado General', 25000.00, 3, 1, 1),
('c6706e81-a32b-49c8-beaa-f492cba36ac4', 'Domicilio', 15000.00, 3, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` bigint(20) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `intentos_fallidos` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `bloqueado_hasta` timestamp NULL DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  `actualizado_en` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `debe_cambiar_contrasena` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `email`, `password_hash`, `intentos_fallidos`, `bloqueado_hasta`, `activo`, `creado_en`, `actualizado_en`, `debe_cambiar_contrasena`) VALUES
(1, 'Jose Daniel', 'Carrascal', 'josedanielcp@gmail.com', '$argon2id$v=19$m=65536,t=3,p=1$DRDuy1qCMPu1L5lFcEk/qQ$ndYBGMtJz07eny3UktJuoL58LtPIf6QbaoWYbMcSlW0', 0, NULL, 1, '2026-08-20 22:42:41', '2026-08-25 20:29:43', 0),
(5, 'Juan', 'Perez', 'juanperez@gmail.com', '$argon2id$v=19$m=65536,t=3,p=1$YEstihsfdoETk4Yr0A2xlw$88vQJ/UuxUc8tBPzQCKj7XpWnzSOnWiFmCuypCFgxxQ', 0, NULL, 1, '2026-08-24 22:46:08', '2026-08-24 22:47:11', 0),
(6, 'Pedro', 'Algun Apellido', 'pedronose@gmail.com', '$argon2id$v=19$m=65536,t=3,p=1$PjoQ54BX/s4TaKjUyQ9Rag$Uv3yrYmkXSkIWpJjD3UJOYFfkwn1Q7yIaUAP8NdYZzg', 0, NULL, 1, '2026-08-24 23:03:04', '2026-08-24 23:04:03', 0),
(7, 'Nombre General', 'Apellidos', 'usuarionormal@gmail.com', '$argon2id$v=19$m=65536,t=3,p=1$H6hQKwYYzpE9W0lLYqgUQg$jDC7o8TEDyPhy6VGcn10WjvRm4OS0axFIzHIVmRuFf8', 0, NULL, 1, '2026-08-26 17:01:11', '2026-08-26 17:02:31', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_rol`
--

CREATE TABLE `usuario_rol` (
  `id_usuario` bigint(20) UNSIGNED NOT NULL,
  `id_rol` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario_rol`
--

INSERT INTO `usuario_rol` (`id_usuario`, `id_rol`) VALUES
(1, 1),
(5, 2),
(6, 3),
(7, 4);

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
-- Indices de la tabla `modulos`
--
ALTER TABLE `modulos`
  ADD PRIMARY KEY (`id_modulo`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `permisos`
--
ALTER TABLE `permisos`
  ADD PRIMARY KEY (`id_permiso`),
  ADD UNIQUE KEY `nombre` (`nombre`),
  ADD KEY `fk_permisos_modulos` (`id_modulo`);

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
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `rol_permiso`
--
ALTER TABLE `rol_permiso`
  ADD PRIMARY KEY (`id_rol`,`id_permiso`),
  ADD KEY `idx_rol_permiso_id_permiso` (`id_permiso`);

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
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indices de la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD PRIMARY KEY (`id_usuario`,`id_rol`),
  ADD KEY `idx_usuario_rol_id_rol` (`id_rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `descuentos`
--
ALTER TABLE `descuentos`
  MODIFY `id_descuento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `detalle_facturas`
--
ALTER TABLE `detalle_facturas`
  MODIFY `id_detalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `facturas`
--
ALTER TABLE `facturas`
  MODIFY `id_factura` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `impuestos`
--
ALTER TABLE `impuestos`
  MODIFY `id_impuesto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `inventarios`
--
ALTER TABLE `inventarios`
  MODIFY `id_inventario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `modulos`
--
ALTER TABLE `modulos`
  MODIFY `id_modulo` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `permisos`
--
ALTER TABLE `permisos`
  MODIFY `id_permiso` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT de la tabla `politicas_vencimiento`
--
ALTER TABLE `politicas_vencimiento`
  MODIFY `id_politica` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id_rol` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalle_facturas`
--
ALTER TABLE `detalle_facturas`
  ADD CONSTRAINT `fk_detalle_factura_cabecera` FOREIGN KEY (`id_factura`) REFERENCES `facturas` (`id_factura`) ON DELETE CASCADE;

--
-- Filtros para la tabla `permisos`
--
ALTER TABLE `permisos`
  ADD CONSTRAINT `fk_permisos_modulos` FOREIGN KEY (`id_modulo`) REFERENCES `modulos` (`id_modulo`) ON UPDATE CASCADE;

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
-- Filtros para la tabla `rol_permiso`
--
ALTER TABLE `rol_permiso`
  ADD CONSTRAINT `fk_rol_permiso_permisos` FOREIGN KEY (`id_permiso`) REFERENCES `permisos` (`id_permiso`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rol_permiso_roles` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `servicios`
--
ALTER TABLE `servicios`
  ADD CONSTRAINT `fk_servicios_descuento` FOREIGN KEY (`id_descuento`) REFERENCES `descuentos` (`id_descuento`) ON UPDATE CASCADE,
  ADD CONSTRAINT `servicios_ibfk_1` FOREIGN KEY (`id_impuesto`) REFERENCES `impuestos` (`id_impuesto`);

--
-- Filtros para la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD CONSTRAINT `fk_usuario_rol_roles` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_usuario_rol_usuarios` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
