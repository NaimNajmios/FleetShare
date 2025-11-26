-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Nov 26, 2025 at 06:50 AM
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
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `address_id` int(11) NOT NULL,
  `address_user_id` int(11) NOT NULL,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `postal_code` varchar(20) NOT NULL,
  `effective_start_date` date NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `addresses`
--

INSERT INTO `addresses` (`address_id`, `address_user_id`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `effective_start_date`, `created_at`, `updated_at`) VALUES
(1, 1, 'Level 20, Menara TM', 'Jalan Pantai Baharu', 'Kuala Lumpur', 'Wilayah Persekutuan', '50672', '2023-01-01', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(2, 2, '41, Jalan Tun Razak', 'Taman U-Thant', 'Kuala Lumpur', 'Wilayah Persekutuan', '50400', '2023-02-15', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(3, 3, 'Lot 5, Jalan Pantai', 'Tanjung Aru', 'Kota Kinabalu', 'Sabah', '88100', '2023-03-10', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(4, 4, '15, Persiaran Gurney', 'George Town', 'George Town', 'Pulau Pinang', '10250', '2023-04-05', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(5, 5, '12, Kolej Kediaman', 'Universiti Malaya', 'Kuala Lumpur', 'Wilayah Persekutuan', '50603', '2024-01-10', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(6, 6, 'B-12-1, Pavilion Residences', 'Jalan Bukit Bintang', 'Kuala Lumpur', 'Wilayah Persekutuan', '55100', '2024-01-12', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(7, 7, '45, Jalan Gasing', 'Section 10', 'Petaling Jaya', 'Selangor', '46000', '2024-02-01', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(8, 8, 'Shangri-La Hotel', '11 Jalan Sultan Ismail', 'Kuala Lumpur', 'Wilayah Persekutuan', '50250', '2024-03-15', '2025-11-24 02:56:19', '2025-11-24 02:56:19'),
(9, 9, 'Kg Air', 'Jalan Tuaran', 'Kota Kinabalu', 'Sabah', '88450', '2024-04-20', '2025-11-24 02:56:19', '2025-11-24 02:56:19');

-- --------------------------------------------------------

--
-- Table structure for table `bookingpricesnapshot`
--

CREATE TABLE `bookingpricesnapshot` (
  `booking_id` int(11) NOT NULL,
  `rate_per_day` decimal(10,2) NOT NULL,
  `days_rented` int(11) NOT NULL,
  `total_calculated_cost` decimal(10,2) NOT NULL
) ;

--
-- Dumping data for table `bookingpricesnapshot`
--

INSERT INTO `bookingpricesnapshot` (`booking_id`, `rate_per_day`, `days_rented`, `total_calculated_cost`) VALUES
(101, 220.00, 8, 1760.00),
(102, 350.00, 3, 1350.00),
(103, 115.00, 14, 1610.00),
(104, 120.00, 1, 120.00),
(105, 450.00, 2, 900.00);

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
(101, 3, 7, 3, '2025-03-29 10:00:00', '2025-04-05 18:00:00', '2025-03-01 01:00:00'),
(102, 2, 4, 2, '2025-06-06 08:00:00', '2025-06-09 20:00:00', '2025-05-20 06:00:00'),
(103, 4, 2, 1, '2025-01-05 12:00:00', '2025-01-19 12:00:00', '2024-12-20 02:00:00'),
(104, 1, 1, 1, '2025-05-15 09:00:00', '2025-05-16 09:00:00', '2025-05-10 00:00:00'),
(105, 5, 5, 2, '2025-08-30 14:00:00', '2025-09-01 12:00:00', '2025-08-15 03:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `bookingstatuslog`
--

CREATE TABLE `bookingstatuslog` (
  `booking_log_id` bigint(20) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `status_value` enum('PENDING','CONFIRMED','ACTIVE','COMPLETED','CANCELLED','DISPUTED') NOT NULL,
  `actor_user_id` int(11) DEFAULT NULL,
  `status_timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `remarks` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bookingstatuslog`
--

INSERT INTO `bookingstatuslog` (`booking_log_id`, `booking_id`, `status_value`, `actor_user_id`, `status_timestamp`, `remarks`) VALUES
(1, 101, 'PENDING', 7, '2025-03-01 01:00:00', 'Booking initiated'),
(2, 101, 'CONFIRMED', 1, '2025-03-01 01:06:00', 'Payment received'),
(3, 101, 'ACTIVE', 4, '2025-03-29 02:15:00', 'Vehicle handed over'),
(4, 101, 'COMPLETED', 4, '2025-04-05 10:30:00', 'Vehicle returned safely'),
(5, 102, 'PENDING', 6, '2025-05-20 06:00:00', 'Booking initiated'),
(6, 102, 'CONFIRMED', 1, '2025-05-20 06:10:00', 'Payment received'),
(7, 102, 'ACTIVE', 3, '2025-06-06 00:15:00', 'Vehicle pickup completed'),
(8, 102, 'COMPLETED', 3, '2025-06-09 12:45:00', 'Returned after Sabah adventure'),
(9, 103, 'PENDING', 8, '2024-12-20 02:00:00', 'Booking initiated'),
(10, 103, 'CONFIRMED', 1, '2024-12-20 02:06:00', 'Payment verified by admin'),
(11, 103, 'ACTIVE', 2, '2025-01-05 04:15:00', 'Vehicle handed over to renter'),
(12, 103, 'COMPLETED', 2, '2025-01-19 04:30:00', 'Vehicle returned in good condition'),
(13, 104, 'PENDING', 5, '2025-05-10 00:00:00', 'Booking initiated'),
(14, 104, 'CONFIRMED', 1, '2025-05-10 00:05:00', 'Payment received'),
(15, 104, 'ACTIVE', 2, '2025-05-15 01:10:00', 'Vehicle collected'),
(16, 104, 'COMPLETED', 2, '2025-05-16 01:20:00', 'Returned on time'),
(17, 105, 'PENDING', 9, '2025-08-15 03:00:00', 'Booking initiated'),
(18, 105, 'PENDING', 9, '2025-08-15 03:01:00', 'Waiting for transfer');

-- --------------------------------------------------------

--
-- Table structure for table `fleetowners`
--

CREATE TABLE `fleetowners` (
  `fleet_owner_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `business_name` varchar(255) NOT NULL,
  `contact_phone` varchar(50) DEFAULT NULL,
  `is_verified` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `fleetowners`
--

INSERT INTO `fleetowners` (`fleet_owner_id`, `user_id`, `business_name`, `contact_phone`, `is_verified`, `updated_at`) VALUES
(1, 2, 'MetroCity Car Rental Sdn Bhd', '+603-77214000', 1, '2025-11-24 02:55:07'),
(2, 3, 'Borneo 4x4 Expeditions Ent', '+6088-234567', 1, '2025-11-24 02:55:07'),
(3, 4, 'Prestige Limousines PLT', '+604-2267890', 1, '2025-11-24 02:55:07');

-- --------------------------------------------------------

--
-- Table structure for table `invoices`
--

CREATE TABLE `invoices` (
  `invoice_id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `fleet_owner_id` int(11) NOT NULL,
  `renter_id` int(11) NOT NULL,
  `invoice_number` varchar(100) NOT NULL,
  `issue_date` date NOT NULL,
  `due_date` date NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` enum('DRAFT','ISSUED','PAID','OVERDUE','VOID') DEFAULT 'ISSUED'
) ;

--
-- Dumping data for table `invoices`
--

INSERT INTO `invoices` (`invoice_id`, `booking_id`, `fleet_owner_id`, `renter_id`, `invoice_number`, `issue_date`, `due_date`, `total_amount`, `remarks`, `status`) VALUES
(1, 101, 3, 3, 'INV-2025-001', '2025-03-01', '2025-03-01', 1760.00, 'Hari Raya Rental', 'PAID'),
(2, 102, 2, 2, 'INV-2025-002', '2025-05-20', '2025-05-20', 1350.00, 'Sabah Trip', 'PAID'),
(3, 103, 1, 4, 'INV-2024-099', '2024-12-20', '2024-12-20', 1610.00, 'January Holiday', 'PAID'),
(4, 104, 1, 1, 'INV-2025-045', '2025-05-10', '2025-05-10', 120.00, 'Student Rental', 'PAID'),
(5, 105, 2, 5, 'INV-2025-088', '2025-08-15', '2025-08-20', 900.00, 'Merdeka Weekend', 'ISSUED');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL,
  `invoice_id` int(11) NOT NULL,
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `amount` decimal(10,2) NOT NULL,
  `payment_method` enum('CREDIT_CARD','BANK_TRANSFER','QR_PAYMENT','CASH') NOT NULL,
  `payment_status` enum('PENDING','VERIFIED','FAILED') DEFAULT 'PENDING',
  `transaction_reference` varchar(255) DEFAULT NULL,
  `verification_proof_url` varchar(1024) DEFAULT NULL,
  `verified_by_user_id` int(11) DEFAULT NULL
) ;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`payment_id`, `invoice_id`, `payment_date`, `amount`, `payment_method`, `payment_status`, `transaction_reference`, `verification_proof_url`, `verified_by_user_id`) VALUES
(1, 1, '2025-03-01 01:05:00', 1760.00, 'BANK_TRANSFER', 'VERIFIED', 'FPX_99887711', 'https://receipts/1.pdf', 1),
(2, 2, '2025-05-20 06:05:00', 1350.00, 'CREDIT_CARD', 'VERIFIED', 'ch_3Lz9922aa', 'https://receipts/2.pdf', 1),
(3, 3, '2024-12-20 02:05:00', 1610.00, 'CREDIT_CARD', 'VERIFIED', 'tx_int_8877', 'https://receipts/3.pdf', 1),
(4, 4, '2025-05-10 00:05:00', 120.00, 'QR_PAYMENT', 'VERIFIED', 'tng_223344', 'https://receipts/4.pdf', 1);

-- --------------------------------------------------------

--
-- Table structure for table `paymentstatuslog`
--

CREATE TABLE `paymentstatuslog` (
  `payment_log_id` bigint(20) NOT NULL,
  `payment_id` int(11) NOT NULL,
  `status_value` enum('PENDING','VERIFIED','FAILED') NOT NULL,
  `actor_user_id` int(11) DEFAULT NULL,
  `status_timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `paymentstatuslog`
--

INSERT INTO `paymentstatuslog` (`payment_log_id`, `payment_id`, `status_value`, `actor_user_id`, `status_timestamp`) VALUES
(1, 1, 'PENDING', 7, '2025-03-01 01:05:00'),
(2, 1, 'VERIFIED', 1, '2025-03-01 01:06:00'),
(3, 2, 'PENDING', 6, '2025-05-20 06:05:00'),
(4, 2, 'VERIFIED', 1, '2025-05-20 06:10:00'),
(5, 3, 'PENDING', 8, '2024-12-20 02:05:00'),
(6, 3, 'VERIFIED', 1, '2024-12-20 02:06:00'),
(7, 4, 'PENDING', 5, '2025-05-10 00:05:00'),
(8, 4, 'VERIFIED', 1, '2025-05-10 00:05:30');

-- --------------------------------------------------------

--
-- Table structure for table `platformadmins`
--

CREATE TABLE `platformadmins` (
  `admin_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `platformadmins`
--

INSERT INTO `platformadmins` (`admin_id`, `user_id`, `full_name`, `updated_at`) VALUES
(1, 1, 'Azman bin Khalid', '2025-11-24 02:55:07');

-- --------------------------------------------------------

--
-- Table structure for table `renters`
--

CREATE TABLE `renters` (
  `renter_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `phone_number` varchar(50) DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `renters`
--

INSERT INTO `renters` (`renter_id`, `user_id`, `full_name`, `phone_number`, `updated_at`) VALUES
(1, 5, 'Nurul Izzah binti Ahmad', '+6011-22334455', '2025-11-24 02:55:07'),
(2, 6, 'Jason Lee Keng Wee', '+6012-3456789', '2025-11-24 02:55:07'),
(3, 7, 'Muthu Kumar a/l Balakrishnan', '+6013-9876543', '2025-11-24 02:55:07'),
(4, 8, 'Sarah Jenkins', '+44-7700900000', '2025-11-24 02:55:07'),
(5, 9, 'Awang Damit', '+6019-8765432', '2025-11-24 02:55:07');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  `user_role` enum('PLATFORM_ADMIN','FLEET_OWNER','RENTER') NOT NULL,
  `profile_image_url` varchar(1024) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `email`, `hashed_password`, `user_role`, `profile_image_url`, `created_at`, `is_active`) VALUES
(1, 'admin@rentmy.com.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'PLATFORM_ADMIN', NULL, '2023-01-01 00:00:00', 1),
(2, 'ops@metrocity.com.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'FLEET_OWNER', NULL, '2023-02-15 01:30:00', 1),
(3, 'josephine@borneo4x4.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'FLEET_OWNER', NULL, '2023-03-10 06:20:00', 1),
(4, 'manager@prestigelimo.com', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'FLEET_OWNER', NULL, '2023-04-05 03:00:00', 1),
(5, 'nurul.izzah@student.um.edu.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', NULL, '2024-01-10 02:00:00', 1),
(6, 'jason.lee@corp.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', NULL, '2024-01-12 08:30:00', 1),
(7, 'muthu.kumar@gmail.com', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', NULL, '2024-02-01 01:15:00', 1),
(8, 'sarah.jenkins@ukmail.co.uk', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', NULL, '2024-03-15 05:45:00', 1),
(9, 'awang.damit@sabah.gov.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', NULL, '2024-04-20 00:30:00', 1);

-- --------------------------------------------------------

--
-- Table structure for table `vehiclemaintenance`
--

CREATE TABLE `vehiclemaintenance` (
  `maintenance_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `fleet_owner_id` int(11) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `maintenance_date` date NOT NULL,
  `cost` decimal(10,2) DEFAULT NULL,
  `status` enum('PENDING','IN_PROGRESS','COMPLETED') DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehiclemaintenance`
--

INSERT INTO `vehiclemaintenance` (`maintenance_id`, `vehicle_id`, `fleet_owner_id`, `description`, `maintenance_date`, `cost`, `status`, `created_at`) VALUES
(1, 3, 1, 'Regular Service 20k KM (Oil Change)', '2024-12-01', 250.00, 'COMPLETED', '2024-12-01 02:00:00'),
(2, 1, 1, 'Replace front brake pads', '2024-11-15', 180.00, 'COMPLETED', '2024-11-15 01:00:00'),
(3, 2, 1, '5000km service - Oil and filter change', '2024-10-20', 150.00, 'COMPLETED', '2024-10-20 02:30:00'),
(4, 1, 1, 'Scheduled 30k service', '2025-06-01', 350.00, 'PENDING', '2025-05-25 06:00:00'),
(5, 4, 2, 'Replace air filter and spark plugs', '2024-09-10', 280.00, 'COMPLETED', '2024-09-10 03:00:00'),
(6, 5, 2, 'Major service 30k km - Engine oil, filters, brake check', '2025-02-14', 650.00, 'COMPLETED', '2025-02-14 00:30:00'),
(7, 6, 2, 'Tire rotation and wheel alignment', '2024-12-05', 220.00, 'COMPLETED', '2024-12-05 05:00:00'),
(8, 5, 2, 'Replace rear brake discs', '2025-07-10', 850.00, 'IN_PROGRESS', '2025-07-10 01:00:00'),
(9, 7, 3, '10k service package', '2025-03-20', 380.00, 'COMPLETED', '2025-03-20 02:00:00'),
(10, 8, 3, 'Premium detailing and interior cleaning', '2025-01-25', 450.00, 'COMPLETED', '2025-01-25 06:30:00'),
(11, 9, 3, 'BMW comprehensive inspection', '2024-11-30', 550.00, 'COMPLETED', '2024-11-30 03:00:00'),
(12, 8, 3, 'Air conditioning system service', '2025-05-15', 420.00, 'COMPLETED', '2025-05-15 07:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `vehiclepricehistory`
--

CREATE TABLE `vehiclepricehistory` (
  `price_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `rate_per_day` decimal(10,2) NOT NULL,
  `effective_start_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ;

--
-- Dumping data for table `vehiclepricehistory`
--

INSERT INTO `vehiclepricehistory` (`price_id`, `vehicle_id`, `rate_per_day`, `effective_start_date`) VALUES
(1, 1, 120.00, '2023-12-31 16:00:00'),
(2, 2, 115.00, '2023-12-31 16:00:00'),
(3, 3, 90.00, '2023-12-31 16:00:00'),
(4, 4, 350.00, '2023-12-31 16:00:00'),
(5, 5, 450.00, '2023-12-31 16:00:00'),
(6, 6, 500.00, '2023-12-31 16:00:00'),
(7, 7, 220.00, '2023-12-31 16:00:00'),
(8, 8, 850.00, '2023-12-31 16:00:00'),
(9, 9, 900.00, '2023-12-31 16:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

CREATE TABLE `vehicles` (
  `vehicle_id` int(11) NOT NULL,
  `fleet_owner_id` int(11) NOT NULL,
  `model` varchar(100) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `manufacturing_year` int(11) DEFAULT NULL,
  `registration_no` varchar(20) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `fuel_type` varchar(50) DEFAULT NULL,
  `transmission_type` varchar(50) DEFAULT NULL,
  `mileage` int(11) DEFAULT NULL,
  `vehicle_image_url` varchar(1024) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`vehicle_id`, `fleet_owner_id`, `model`, `brand`, `manufacturing_year`, `registration_no`, `category`, `fuel_type`, `transmission_type`, `mileage`, `vehicle_image_url`, `created_at`, `updated_at`) VALUES
(1, 1, 'Myvi 1.5 AV', 'Perodua', 2023, 'VHJ 8821', 'Hatchback', 'PETROL', 'AUTO', 15000, 'https://img/myvi.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(2, 1, 'Bezza 1.3 Advance', 'Perodua', 2024, 'VKE 4402', 'Sedan', 'PETROL', 'AUTO', 5000, 'https://img/bezza.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(3, 1, 'Saga 1.3 Premium S', 'Proton', 2023, 'BRA 3022', 'Sedan', 'PETROL', 'AUTO', 22000, 'https://img/saga.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(4, 2, 'X70 1.8 TGDi Premium', 'Proton', 2022, 'SYC 5519', 'SUV', 'PETROL', 'AUTO', 35000, 'https://img/x70.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(5, 2, 'Hilux 2.8 Rogue', 'Toyota', 2023, 'SAB 9921 A', 'Pickup 4x4', 'DIESEL', 'AUTO', 28000, 'https://img/hilux.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(6, 2, 'Ranger Wildtrak', 'Ford', 2023, 'QAA 1122', 'Pickup 4x4', 'DIESEL', 'AUTO', 12000, 'https://img/ranger.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(7, 3, 'City 1.5 V Sensing', 'Honda', 2024, 'PQA 8888', 'Sedan', 'PETROL', 'AUTO', 8000, 'https://img/city.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(8, 3, 'Vellfire 2.5 ZG', 'Toyota', 2023, 'V 1', 'Luxury MPV', 'PETROL', 'AUTO', 15000, 'https://img/vellfire.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33'),
(9, 3, '320i M Sport', 'BMW', 2023, 'PPP 77', 'Luxury Sedan', 'PETROL', 'AUTO', 9000, 'https://img/bmw.jpg', '2025-11-24 02:58:33', '2025-11-24 02:58:33');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`address_id`),
  ADD KEY `address_user_id` (`address_user_id`);

--
-- Indexes for table `bookingpricesnapshot`
--
ALTER TABLE `bookingpricesnapshot`
  ADD PRIMARY KEY (`booking_id`);

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `renter_id` (`renter_id`),
  ADD KEY `vehicle_id` (`vehicle_id`),
  ADD KEY `fleet_owner_id` (`fleet_owner_id`);

--
-- Indexes for table `bookingstatuslog`
--
ALTER TABLE `bookingstatuslog`
  ADD PRIMARY KEY (`booking_log_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `actor_user_id` (`actor_user_id`);

--
-- Indexes for table `fleetowners`
--
ALTER TABLE `fleetowners`
  ADD PRIMARY KEY (`fleet_owner_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`invoice_id`),
  ADD UNIQUE KEY `invoice_number` (`invoice_number`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `fleet_owner_id` (`fleet_owner_id`),
  ADD KEY `renter_id` (`renter_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `invoice_id` (`invoice_id`),
  ADD KEY `verified_by_user_id` (`verified_by_user_id`);

--
-- Indexes for table `paymentstatuslog`
--
ALTER TABLE `paymentstatuslog`
  ADD PRIMARY KEY (`payment_log_id`),
  ADD KEY `payment_id` (`payment_id`),
  ADD KEY `actor_user_id` (`actor_user_id`);

--
-- Indexes for table `platformadmins`
--
ALTER TABLE `platformadmins`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `renters`
--
ALTER TABLE `renters`
  ADD PRIMARY KEY (`renter_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `vehiclemaintenance`
--
ALTER TABLE `vehiclemaintenance`
  ADD PRIMARY KEY (`maintenance_id`),
  ADD KEY `vehicle_id` (`vehicle_id`),
  ADD KEY `fleet_owner_id` (`fleet_owner_id`);

--
-- Indexes for table `vehiclepricehistory`
--
ALTER TABLE `vehiclepricehistory`
  ADD PRIMARY KEY (`price_id`),
  ADD KEY `vehicle_id` (`vehicle_id`);

--
-- Indexes for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD PRIMARY KEY (`vehicle_id`),
  ADD UNIQUE KEY `uk_registration_no` (`registration_no`,`fleet_owner_id`),
  ADD KEY `fleet_owner_id` (`fleet_owner_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `addresses`
--
ALTER TABLE `addresses`
  MODIFY `address_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bookingstatuslog`
--
ALTER TABLE `bookingstatuslog`
  MODIFY `booking_log_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `fleetowners`
--
ALTER TABLE `fleetowners`
  MODIFY `fleet_owner_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `invoice_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `paymentstatuslog`
--
ALTER TABLE `paymentstatuslog`
  MODIFY `payment_log_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `platformadmins`
--
ALTER TABLE `platformadmins`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `renters`
--
ALTER TABLE `renters`
  MODIFY `renter_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `vehiclemaintenance`
--
ALTER TABLE `vehiclemaintenance`
  MODIFY `maintenance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `vehiclepricehistory`
--
ALTER TABLE `vehiclepricehistory`
  MODIFY `price_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `vehicle_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`address_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `bookingpricesnapshot`
--
ALTER TABLE `bookingpricesnapshot`
  ADD CONSTRAINT `bookingpricesnapshot_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE;

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`renter_id`) REFERENCES `renters` (`renter_id`),
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`),
  ADD CONSTRAINT `bookings_ibfk_3` FOREIGN KEY (`fleet_owner_id`) REFERENCES `fleetowners` (`fleet_owner_id`);

--
-- Constraints for table `bookingstatuslog`
--
ALTER TABLE `bookingstatuslog`
  ADD CONSTRAINT `bookingstatuslog_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  ADD CONSTRAINT `bookingstatuslog_ibfk_2` FOREIGN KEY (`actor_user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `fleetowners`
--
ALTER TABLE `fleetowners`
  ADD CONSTRAINT `fleetowners_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  ADD CONSTRAINT `invoices_ibfk_2` FOREIGN KEY (`fleet_owner_id`) REFERENCES `fleetowners` (`fleet_owner_id`),
  ADD CONSTRAINT `invoices_ibfk_3` FOREIGN KEY (`renter_id`) REFERENCES `renters` (`renter_id`);

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`invoice_id`),
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`verified_by_user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `paymentstatuslog`
--
ALTER TABLE `paymentstatuslog`
  ADD CONSTRAINT `paymentstatuslog_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`),
  ADD CONSTRAINT `paymentstatuslog_ibfk_2` FOREIGN KEY (`actor_user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `platformadmins`
--
ALTER TABLE `platformadmins`
  ADD CONSTRAINT `platformadmins_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `renters`
--
ALTER TABLE `renters`
  ADD CONSTRAINT `renters_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `vehiclemaintenance`
--
ALTER TABLE `vehiclemaintenance`
  ADD CONSTRAINT `vehiclemaintenance_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `vehiclemaintenance_ibfk_2` FOREIGN KEY (`fleet_owner_id`) REFERENCES `fleetowners` (`fleet_owner_id`);

--
-- Constraints for table `vehiclepricehistory`
--
ALTER TABLE `vehiclepricehistory`
  ADD CONSTRAINT `vehiclepricehistory_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON DELETE CASCADE;

--
-- Constraints for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD CONSTRAINT `vehicles_ibfk_1` FOREIGN KEY (`fleet_owner_id`) REFERENCES `fleetowners` (`fleet_owner_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
