-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Apr 04, 2026 at 04:16 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fleetshareplayground`
--

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `booking_id` int(11) NOT NULL,
  `renter_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `fleet_owner_id` int(11) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`booking_id`, `renter_id`, `vehicle_id`, `fleet_owner_id`, `start_date`, `end_date`, `created_at`) VALUES
(101, 3, 7, 3, '2025-05-29 10:00:00', '2025-06-05 18:00:00', '2025-05-01 01:00:00'),
(102, 2, 4, 2, '2025-08-06 08:00:00', '2025-08-09 20:00:00', '2025-07-20 06:00:00'),
(103, 4, 2, 1, '2025-03-05 12:00:00', '2025-03-19 12:00:00', '2025-02-20 02:00:00'),
(104, 1, 1, 1, '2025-07-15 09:00:00', '2025-07-16 09:00:00', '2025-07-10 00:00:00'),
(105, 5, 5, 2, '2025-10-30 14:00:00', '2025-11-01 12:00:00', '2025-10-15 03:00:00'),
(112, 8, 11, 1, '2026-03-25 00:00:00', '2026-03-28 23:59:59', '2026-03-19 03:37:07'),
(113, 8, 10, 1, '2026-03-28 00:00:00', '2026-03-31 23:59:59', '2026-03-19 03:44:31'),
(114, 6, 8, 3, '2026-03-30 00:00:00', '2026-04-02 23:59:59', '2026-03-20 11:56:15'),
(115, 6, 10, 1, '2026-03-30 00:00:00', '2026-04-06 23:59:59', '2026-03-20 11:57:31'),
(116, 6, 1, 1, '2026-03-23 00:00:00', '2026-03-24 23:59:59', '2026-03-21 01:22:53'),
(117, 6, 11, 1, '2026-03-21 00:00:00', '2026-03-24 23:59:59', '2026-03-21 02:18:59'),
(118, 6, 10, 1, '2026-05-01 00:00:00', '2026-05-05 23:59:00', '2026-04-25 03:50:18'),
(119, 6, 10, 1, '2026-05-10 00:00:00', '2026-05-13 23:59:59', '2026-04-25 07:52:20');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `renter_id` (`renter_id`),
  ADD KEY `vehicle_id` (`vehicle_id`),
  ADD KEY `fleet_owner_id` (`fleet_owner_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`renter_id`) REFERENCES `renters` (`renter_id`),
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`),
  ADD CONSTRAINT `bookings_ibfk_3` FOREIGN KEY (`fleet_owner_id`) REFERENCES `fleetowners` (`fleet_owner_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
