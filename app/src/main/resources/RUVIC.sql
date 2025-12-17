-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Servidor: mysql:3306
-- Tiempo de generación: 17-12-2025 a las 13:47:38
-- Versión del servidor: 8.0.43
-- Versión de PHP: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `RUVIC`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ADDRESS`
--

CREATE TABLE `ADDRESS` (
  `id` int NOT NULL,
  `street` varchar(255) NOT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `zip_code` varchar(20) NOT NULL,
  `country` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `ADDRESS`
--

INSERT INTO `ADDRESS` (`id`, `street`, `city`, `state`, `zip_code`, `country`) VALUES
(5, 'C. Ramon Lull', 'Santpedor', 'Barcelona', '08250', 'España'),
(6, 'Calle Sonsoles', 'Madrid', 'Madrid', '34023', 'España'),
(7, 'Calle hotel', 'manresa', 'bcn', '20445', 'spain'),
(9, 'Carrer Miquel Llor 18', 'Vic', 'BCN', '08500', 'Spain'),
(10, 'Calle la riera 212', 'Castellnou de bages', 'bcn', '08251', 'Spain');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `PAYMENT`
--

CREATE TABLE `PAYMENT` (
  `id` int NOT NULL,
  `reservation_id` int DEFAULT NULL,
  `amount` decimal(6,2) NOT NULL,
  `payment_date` date NOT NULL,
  `payment_method` varchar(100) NOT NULL,
  `status` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `RESERVATION`
--

CREATE TABLE `RESERVATION` (
  `id` int NOT NULL,
  `student_id` int NOT NULL,
  `room_id` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `RESERVATION_SERVICE`
--

CREATE TABLE `RESERVATION_SERVICE` (
  `reservation_id` int NOT NULL,
  `service_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ROOM`
--

CREATE TABLE `ROOM` (
  `id` int NOT NULL,
  `room_number` varchar(10) NOT NULL,
  `floor` int NOT NULL,
  `capacity` int NOT NULL,
  `room_type` varchar(50) NOT NULL,
  `price` decimal(6,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `SERVICE`
--

CREATE TABLE `SERVICE` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` decimal(5,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `STUDENT`
--

CREATE TABLE `STUDENT` (
  `id` int NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `address_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `STUDENT`
--

INSERT INTO `STUDENT` (`id`, `first_name`, `last_name`, `email`, `password_hash`, `phone_number`, `address_id`) VALUES
(3, 'Diego Sniper', 'Fornti', 'diegosniper@gmail.com', 'diego123.', '593556804', 5),
(4, 'Juan', 'Lopez', 'juanlopez@gmail.com', 'juan123.', '680678489', 6),
(5, 'Gil', 'Farres', 'gilfarres@gmail.com', '', '45693212', 7);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `ADDRESS`
--
ALTER TABLE `ADDRESS`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `PAYMENT`
--
ALTER TABLE `PAYMENT`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reservation_id` (`reservation_id`),
  ADD UNIQUE KEY `PAYMENT_reservation_id_UNIQUE` (`reservation_id`);

--
-- Indices de la tabla `RESERVATION`
--
ALTER TABLE `RESERVATION`
  ADD PRIMARY KEY (`id`),
  ADD KEY `RESERVATION_student_id_FK` (`student_id`),
  ADD KEY `RESERVATION_room_id_FK` (`room_id`);

--
-- Indices de la tabla `RESERVATION_SERVICE`
--
ALTER TABLE `RESERVATION_SERVICE`
  ADD PRIMARY KEY (`reservation_id`,`service_id`),
  ADD KEY `RESERVATION_SERVICE_ibfk_2` (`service_id`);

--
-- Indices de la tabla `ROOM`
--
ALTER TABLE `ROOM`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ROOM_UK` (`room_number`);

--
-- Indices de la tabla `SERVICE`
--
ALTER TABLE `SERVICE`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `STUDENT`
--
ALTER TABLE `STUDENT`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `STUDENT_UK` (`email`),
  ADD UNIQUE KEY `address_id` (`address_id`),
  ADD UNIQUE KEY `STUDENT_address_id_UNIQUE` (`address_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `ADDRESS`
--
ALTER TABLE `ADDRESS`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `PAYMENT`
--
ALTER TABLE `PAYMENT`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `RESERVATION`
--
ALTER TABLE `RESERVATION`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ROOM`
--
ALTER TABLE `ROOM`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `SERVICE`
--
ALTER TABLE `SERVICE`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `STUDENT`
--
ALTER TABLE `STUDENT`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `PAYMENT`
--
ALTER TABLE `PAYMENT`
  ADD CONSTRAINT `PAYMENT_FK1` FOREIGN KEY (`reservation_id`) REFERENCES `RESERVATION` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `RESERVATION`
--
ALTER TABLE `RESERVATION`
  ADD CONSTRAINT `RESERVATION_FK1` FOREIGN KEY (`student_id`) REFERENCES `STUDENT` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `RESERVATION_FK2` FOREIGN KEY (`room_id`) REFERENCES `ROOM` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `RESERVATION_SERVICE`
--
ALTER TABLE `RESERVATION_SERVICE`
  ADD CONSTRAINT `RESERVATION_SERVICE_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `RESERVATION` (`id`),
  ADD CONSTRAINT `RESERVATION_SERVICE_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `SERVICE` (`id`);

--
-- Filtros para la tabla `STUDENT`
--
ALTER TABLE `STUDENT`
  ADD CONSTRAINT `STUDENT_ibfk_1` FOREIGN KEY (`address_id`) REFERENCES `ADDRESS` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
