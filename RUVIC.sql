-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Servidor: mysql
-- Tiempo de generación: 04-06-2025 a las 14:58:17
-- Versión del servidor: 8.0.41
-- Versión de PHP: 8.2.27

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

DROP DATABASE IF EXISTS `RUVIC`;
CREATE DATABASE IF NOT EXISTS `RUVIC` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `RUVIC`;


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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `PAYMENT`
--

CREATE TABLE `PAYMENT` (
  `id` int NOT NULL,
  `reservation_id` int UNIQUE, -- Relación uno a uno con RESERVATION
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
  `address_id` int UNIQUE -- Relación uno a uno con ADDRESS
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  ADD UNIQUE KEY `STUDENT_address_id_UNIQUE` (`address_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `ADDRESS`
--
ALTER TABLE `ADDRESS`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

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
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

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