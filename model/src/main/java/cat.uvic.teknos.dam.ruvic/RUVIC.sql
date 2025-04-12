CREATE DATABASE RUVIC;
USE RUVIC;

CREATE TABLE `ADDRESS` (
  `id_addresses` int PRIMARY KEY AUTO_INCREMENT,
  `street` varchar(255) NOT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `zip_code` varchar(20) NOT NULL,
  `country` varchar(100) NOT NULL
);

CREATE TABLE `STUDENT` (
  `id_students` int PRIMARY KEY AUTO_INCREMENT,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(150) UNIQUE NOT NULL,
  `password_hash` varchar(30) NOT NULL,
  `phone_number` varchar(20),
  `address_id` int UNIQUE NOT NULL
);

CREATE TABLE `ROOM` (
  `id_rooms` int PRIMARY KEY AUTO_INCREMENT,
  `room_number` varchar(10) UNIQUE NOT NULL,
  `floor` int NOT NULL,
  `capacity` int NOT NULL,
  `type` enum('Individual','Double','Suite') NOT NULL,
  `price` decimal(6,2) NOT NULL
);

CREATE TABLE `RESERVATION` (
  `id_reservations` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `room_id` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('Pending','Confirmed','Cancelled') NOT NULL
);

CREATE TABLE `PAYMENT` (
  `id_payments` int PRIMARY KEY AUTO_INCREMENT,
  `reservation_id` int UNIQUE NOT NULL,
  `amount` decimal(6,2) NOT NULL,
  `payment_date` date NOT NULL,
  `payment_method` enum('Card','Transfer') NOT NULL,
  `status` enum('Completed','Pending') NOT NULL
);

CREATE TABLE `SERVICE` (
  `id_services` int PRIMARY KEY AUTO_INCREMENT,
  `service_name` varchar(100) NOT NULL,
  `description` varchar(255),
  `price` decimal(5,2) NOT NULL
);

CREATE TABLE `RESERVATION_SERVICE` (
  `reservation_id` int NOT NULL,
  `service_id` int NOT NULL,
  PRIMARY KEY (`reservation_id`, `service_id`)
);

ALTER TABLE `ADDRESS` ADD FOREIGN KEY (`id_addresses`) REFERENCES `STUDENT` (`address_id`);

ALTER TABLE `RESERVATION` ADD FOREIGN KEY (`user_id`) REFERENCES `STUDENT` (`id_students`);

ALTER TABLE `RESERVATION` ADD FOREIGN KEY (`room_id`) REFERENCES `ROOM` (`id_rooms`);

ALTER TABLE `PAYMENT` ADD FOREIGN KEY (`reservation_id`) REFERENCES `RESERVATION` (`id_reservations`);

ALTER TABLE `RESERVATION_SERVICE` ADD FOREIGN KEY (`reservation_id`) REFERENCES `RESERVATION` (`id_reservations`);

ALTER TABLE `RESERVATION_SERVICE` ADD FOREIGN KEY (`service_id`) REFERENCES `SERVICE` (`id_services`);
