-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Feb 26, 2026 at 05:21 AM
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
  `address_id` bigint(20) NOT NULL,
  `address_user_id` int(11) NOT NULL,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `postal_code` varchar(20) NOT NULL,
  `effective_start_date` date NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `addresses`
--

INSERT INTO `addresses` (`address_id`, `address_user_id`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `effective_start_date`, `created_at`, `updated_at`, `latitude`, `longitude`) VALUES
(1, 1, 'Level 20, Menara TM', 'Jalan Pantai Baharu', 'Kuala Lumpur', 'Wilayah Persekutuan', '50672', '2023-01-01', '2025-11-24 02:56:19', '2025-12-16 08:14:06', 5.40716132, 103.08794840),
(2, 2, '41, Jalan Tun Abdul Razak', 'Taman U-Thant', 'Kuala Lumpur', 'Wilayah Persekutuan', '50400', '2023-02-15', '2025-11-24 02:56:19', '2026-01-01 10:37:24', 5.40716132, 103.08794840),
(3, 3, 'Lot 5, Jalan Pantai', 'Tanjung Aru', 'Kota Kinabalu', 'Sabah', '88100', '2023-03-10', '2025-11-24 02:56:19', '2025-12-16 08:14:06', 5.40716132, 103.08794840),
(4, 4, '15, Persiaran Gurney', 'George Town', 'George Town', 'Pulau Pinang', '10250', '2023-04-05', '2025-11-24 02:56:19', '2025-12-16 08:14:06', 5.40716132, 103.08794840),
(5, 5, '12, Kolej Kediaman', 'Universiti Malaya', 'Kuala Lumpur', 'Wilayah Persekutuan', '50603', '2024-01-10', '2025-11-24 02:56:19', '2026-01-01 12:28:54', 5.40716132, 103.08794840),
(6, 6, 'B-12-1, Pavilion Residences', 'Jalan Bukit Bintang', 'Kuala Lumpur', 'Wilayah Persekutuan', '55100', '2024-01-12', '2025-11-24 02:56:19', '2025-12-16 08:14:06', 5.40716132, 103.08794840),
(7, 7, '45, Jalan Gasing', 'Section 10', 'Petaling Jaya', 'Selangor', '46000', '2024-02-01', '2025-11-24 02:56:19', '2025-12-16 08:14:06', 5.40716132, 103.08794840),
(8, 8, 'Shangri-La Hotel', '11 Jalan Sultan Ismail', 'Kuala Lumpur', 'Wilayah Persekutuan', '50250', '2024-03-15', '2025-11-24 02:56:19', '2025-12-16 08:14:06', 5.40716132, 103.08794840),
(9, 9, 'Kg Air', 'Jalan Tuaran', 'Kota Kinabalu', 'Sabah', '88450', '2024-04-20', '2025-11-24 02:56:19', '2025-12-16 08:14:06', 5.40716132, 103.08794840),
(10, 2, '41, Jalan Tun Abdul Razak', 'Taman U-Thant', 'Kuala Lumpur', 'Wilayah Persekutuan', '50400', '2026-01-01', '2026-01-01 11:01:21', '2026-02-26 04:04:03', 5.40987700, 103.07720600),
(11, 2, '41, Jalan Abdul Tun Razak', 'Taman U-Thant', 'Kuala Lumpur', 'Wilayah Persekutuan', '50400', '2026-01-01', '2026-01-01 11:04:47', '2026-01-01 11:04:47', 5.40716132, 103.08794840),
(12, 2, '41, Jalan Tun Abdul Razak', 'Taman U-Thant', 'Kuala Lumpur', 'Wilayah Persekutuan', '50400', '2026-01-01', '2026-01-01 11:12:05', '2026-01-01 11:12:05', 5.40716132, 103.08794840),
(13, 10, 'No. 247', 'Jalan Sultan Mahmud', 'Kuala Terengganu', 'Terengganu', '20000', '2026-01-03', '2026-01-03 03:26:12', '2026-01-20 02:05:07', NULL, NULL),
(14, 11, '41, Jalan Tun Abdul Razak', 'Taman U-Thant', 'Kuala Lumpur', 'Wilayah Persekutuan', '50400', '2026-01-06', '2026-01-06 03:19:07', '2026-01-19 03:20:40', NULL, NULL),
(15, 12, '124, Kolej Kediaman', 'Universiti Malaysia Terengganu', 'Kuala Nerus', 'Terengganu', '21030', '2026-01-19', '2026-01-19 03:34:45', '2026-01-19 03:46:57', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `bookingpricesnapshot`
--

CREATE TABLE `bookingpricesnapshot` (
  `booking_id` int(11) NOT NULL,
  `rate_per_day` decimal(10,2) NOT NULL,
  `days_rented` int(11) NOT NULL,
  `total_calculated_cost` decimal(10,2) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL
) ;

--
-- Dumping data for table `bookingpricesnapshot`
--

INSERT INTO `bookingpricesnapshot` (`booking_id`, `rate_per_day`, `days_rented`, `total_calculated_cost`, `remarks`) VALUES
(101, 220.00, 8, 1760.00, NULL),
(102, 350.00, 3, 1350.00, NULL),
(103, 115.00, 14, 1610.00, NULL),
(104, 120.00, 1, 120.00, NULL),
(105, 450.00, 2, 900.00, NULL),
(112, 150.00, 3, 450.00, NULL),
(113, 130.00, 3, 390.00, NULL),
(114, 850.00, 3, 2550.00, NULL),
(115, 130.00, 7, 910.00, NULL),
(116, 120.00, 1, 120.00, NULL),
(117, 150.00, 3, 450.00, NULL),
(118, 130.00, 4, 520.00, NULL),
(119, 130.00, 3, 390.00, NULL);

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
(105, 5, 5, 2, '2025-08-30 14:00:00', '2025-09-01 12:00:00', '2025-08-15 03:00:00'),
(112, 8, 11, 1, '2026-01-25 00:00:00', '2026-01-28 23:59:59', '2026-01-19 03:37:07'),
(113, 8, 10, 1, '2026-01-28 00:00:00', '2026-01-31 23:59:59', '2026-01-19 03:44:31'),
(114, 6, 8, 3, '2026-01-30 00:00:00', '2026-02-02 23:59:59', '2026-01-20 11:56:15'),
(115, 6, 10, 1, '2026-01-30 00:00:00', '2026-02-06 23:59:59', '2026-01-20 11:57:31'),
(116, 6, 1, 1, '2026-01-23 00:00:00', '2026-01-24 23:59:59', '2026-01-21 01:22:53'),
(117, 6, 11, 1, '2026-01-21 00:00:00', '2026-01-24 23:59:59', '2026-01-21 02:18:59'),
(118, 6, 10, 1, '2026-03-01 00:00:00', '2026-03-05 23:59:00', '2026-02-25 03:50:18'),
(119, 6, 10, 1, '2026-03-10 00:00:00', '2026-03-13 23:59:59', '2026-02-25 07:52:20');

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
(18, 105, 'PENDING', 9, '2025-08-15 03:01:00', 'Waiting for transfer'),
(28, 112, 'PENDING', 12, '2026-01-19 03:37:07', 'Booking initiated by renter'),
(29, 113, 'PENDING', 12, '2026-01-19 03:44:31', 'Booking initiated by renter'),
(30, 112, 'CONFIRMED', 2, '2026-01-19 03:56:48', 'Booking approved'),
(31, 112, 'ACTIVE', 2, '2026-01-19 03:57:35', 'Fund sufficient and handed'),
(32, 112, 'COMPLETED', 2, '2026-01-19 03:58:20', 'Vehicle returned under good condition'),
(33, 113, 'CONFIRMED', 2, '2026-01-19 04:00:21', 'Payment approved'),
(34, 113, 'ACTIVE', 2, '2026-01-19 04:00:34', 'Client pickup the vehicle'),
(35, 113, 'COMPLETED', 2, '2026-01-19 04:00:47', 'Client returned the vehicle'),
(36, 114, 'PENDING', 10, '2026-01-20 11:56:15', 'Booking initiated by renter'),
(37, 115, 'PENDING', 10, '2026-01-20 11:57:31', 'Booking initiated by renter'),
(38, 115, 'CONFIRMED', 2, '2026-01-20 11:58:58', 'Client has paid'),
(39, 115, 'ACTIVE', 2, '2026-01-20 11:59:20', 'Client has pickup the vehicle'),
(40, 115, 'COMPLETED', 2, '2026-01-20 11:59:46', 'Vehicle returned in good condition'),
(41, 116, 'PENDING', 10, '2026-01-21 01:22:54', 'Booking initiated by renter'),
(42, 117, 'PENDING', 10, '2026-01-21 02:18:59', 'Booking initiated by renter'),
(43, 117, 'CONFIRMED', 2, '2026-01-21 02:21:58', 'User has paid'),
(44, 117, 'ACTIVE', 2, '2026-01-21 02:22:54', 'Client has pickup'),
(45, 117, 'COMPLETED', 2, '2026-01-21 02:24:02', 'Client has returned'),
(46, 118, 'PENDING', 10, '2026-02-25 03:50:18', 'Booking initiated by renter'),
(47, 119, 'PENDING', 10, '2026-02-25 07:52:20', 'Booking initiated by renter'),
(48, 119, 'CONFIRMED', 2, '2026-02-25 07:53:12', 'Booking confirmed by owner');

-- --------------------------------------------------------

--
-- Table structure for table `fleetowners`
--

CREATE TABLE `fleetowners` (
  `fleet_owner_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `business_name` varchar(255) NOT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `is_verified` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `payment_qr_url` varchar(1024) DEFAULT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `bank_account_number` varchar(50) DEFAULT NULL,
  `bank_account_holder` varchar(100) DEFAULT NULL,
  `toyyibpay_secret_key` varchar(255) DEFAULT NULL,
  `toyyibpay_category_code` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `fleetowners`
--

INSERT INTO `fleetowners` (`fleet_owner_id`, `user_id`, `business_name`, `contact_phone`, `is_verified`, `updated_at`, `payment_qr_url`, `bank_name`, `bank_account_number`, `bank_account_holder`, `toyyibpay_secret_key`, `toyyibpay_category_code`) VALUES
(1, 2, 'MetroCity Car Rental Sdn Bhd', '+60377214000', 1, '2026-02-26 04:04:03', '/uploads/qrcodes/qr-1-1772078352238-3b801183.png', 'Maybank', '163064403943', 'MetroCity Car Rental Sdn Bhd', NULL, NULL),
(2, 3, 'Borneo 4x4 Expeditions Ent', '+6088-234567', 1, '2025-11-24 02:55:07', NULL, NULL, NULL, NULL, NULL, NULL),
(3, 4, 'Prestige Limousines PLT', '+604-2267890', 1, '2025-11-24 02:55:07', NULL, NULL, NULL, NULL, NULL, NULL);

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
(5, 105, 2, 5, 'INV-2025-088', '2025-08-15', '2025-08-20', 900.00, 'Merdeka Weekend', 'ISSUED'),
(12, 112, 1, 8, 'INV-1768793827628', '2026-01-19', '2026-01-26', 450.00, 'Auto-generated invoice for Booking #112', 'PAID'),
(13, 113, 1, 8, 'INV-1768794271621', '2026-01-19', '2026-01-26', 390.00, 'Auto-generated invoice for Booking #113', 'PAID'),
(14, 114, 3, 6, 'INV-1768910175534', '2026-01-20', '2026-01-27', 2550.00, 'Auto-generated invoice for Booking #114', 'ISSUED'),
(15, 115, 1, 6, 'INV-1768910251947', '2026-01-20', '2026-01-27', 910.00, 'Auto-generated invoice for Booking #115', 'PAID'),
(16, 116, 1, 6, 'INV-1768958574024', '2026-01-21', '2026-01-28', 120.00, 'Auto-generated invoice for Booking #116', 'ISSUED'),
(17, 117, 1, 6, 'INV-1768961939477', '2026-01-21', '2026-01-28', 450.00, 'Auto-generated invoice for Booking #117', 'PAID'),
(18, 118, 1, 6, 'INV-1771991418597', '2026-02-25', '2026-03-04', 520.00, 'Auto-generated invoice for Booking #118', 'ISSUED'),
(19, 119, 1, 6, 'INV-1772005940960', '2026-02-25', '2026-03-04', 390.00, 'Auto-generated invoice for Booking #119', 'ISSUED');

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
(4, 4, '2025-05-10 00:05:00', 120.00, 'QR_PAYMENT', 'VERIFIED', 'tng_223344', 'https://receipts/4.pdf', 1),
(11, 12, '2026-01-19 03:44:08', 450.00, 'CASH', 'VERIFIED', 'CASH-1768794248864', NULL, 2),
(12, 13, '2026-01-19 03:44:57', 390.00, 'BANK_TRANSFER', 'VERIFIED', 'TRANSFER-1768794297830', '/uploads/payments/payment-113-1768794297823-ed3dc4a0.pdf', 2),
(13, 14, '2026-01-20 11:56:43', 2550.00, 'CASH', 'FAILED', 'CASH-1768910203204', NULL, NULL),
(14, 15, '2026-01-20 11:57:57', 910.00, 'BANK_TRANSFER', 'VERIFIED', 'TRANSFER-1768910277500', '/uploads/payments/payment-115-1768910277494-7852ba10.pdf', 2),
(15, 14, '2026-01-21 01:22:26', 2550.00, 'BANK_TRANSFER', 'PENDING', 'BANK_TRANSFER-1768958546826', NULL, NULL),
(16, 17, '2026-01-21 02:20:00', 450.00, 'BANK_TRANSFER', 'VERIFIED', 'TRANSFER-1768962000482', '/uploads/payments/payment-117-1768962000471-a4e3885b.pdf', 2),
(17, 19, '2026-02-25 07:52:28', 390.00, 'CASH', 'PENDING', 'CASH-1772005948404', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `paymentstatuslog`
--

CREATE TABLE `paymentstatuslog` (
  `payment_log_id` bigint(20) NOT NULL,
  `payment_id` int(11) NOT NULL,
  `status_value` enum('PENDING','VERIFIED','FAILED') NOT NULL,
  `actor_user_id` int(11) DEFAULT NULL,
  `status_timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `remarks` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `paymentstatuslog`
--

INSERT INTO `paymentstatuslog` (`payment_log_id`, `payment_id`, `status_value`, `actor_user_id`, `status_timestamp`, `remarks`) VALUES
(1, 1, 'PENDING', 7, '2025-03-01 01:05:00', NULL),
(2, 1, 'VERIFIED', 1, '2025-03-01 01:06:00', NULL),
(3, 2, 'PENDING', 6, '2025-05-20 06:05:00', NULL),
(4, 2, 'VERIFIED', 1, '2025-05-20 06:10:00', NULL),
(5, 3, 'PENDING', 8, '2024-12-20 02:05:00', NULL),
(6, 3, 'VERIFIED', 1, '2024-12-20 02:06:00', NULL),
(7, 4, 'PENDING', 5, '2025-05-10 00:05:00', NULL),
(8, 4, 'VERIFIED', 1, '2025-05-10 00:05:30', NULL),
(14, 11, 'PENDING', 12, '2026-01-19 03:44:08', 'Cash payment option selected by renter'),
(15, 12, 'PENDING', 12, '2026-01-19 03:44:57', 'Bank transfer receipt submitted'),
(16, 11, 'VERIFIED', 2, '2026-01-19 03:58:57', 'Payment verified by owner'),
(17, 12, 'VERIFIED', 2, '2026-01-19 03:59:55', 'Payment verified by owner'),
(18, 13, 'PENDING', 10, '2026-01-20 11:56:43', 'Cash payment option selected by renter'),
(19, 14, 'PENDING', 10, '2026-01-20 11:57:57', 'Bank transfer receipt submitted'),
(20, 14, 'VERIFIED', 2, '2026-01-20 11:58:38', 'Payment verified by owner'),
(21, 13, 'FAILED', 10, '2026-01-21 01:22:26', 'Payment method changed from CASH to BANK_TRANSFER'),
(22, 15, 'PENDING', 10, '2026-01-21 01:22:26', 'Bank transfer payment selected'),
(23, 16, 'PENDING', 10, '2026-01-21 02:20:00', 'Bank transfer receipt submitted'),
(24, 16, 'VERIFIED', 2, '2026-01-21 02:20:59', 'Payment verified by owner'),
(25, 17, 'PENDING', 10, '2026-02-25 07:52:28', 'Cash payment option selected by renter');

-- --------------------------------------------------------

--
-- Table structure for table `platformadmins`
--

CREATE TABLE `platformadmins` (
  `admin_id` bigint(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `platformadmins`
--

INSERT INTO `platformadmins` (`admin_id`, `user_id`, `full_name`, `updated_at`) VALUES
(1, 1, 'Azman bin Khalid Ibrahim', '2026-01-01 11:49:08');

-- --------------------------------------------------------

--
-- Table structure for table `renters`
--

CREATE TABLE `renters` (
  `renter_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `renters`
--

INSERT INTO `renters` (`renter_id`, `user_id`, `full_name`, `phone_number`, `updated_at`) VALUES
(1, 5, 'Nurul Izzah binti Ahmad Jailani', '+6011223344555', '2026-01-01 12:28:54'),
(2, 6, 'Jason Lee Keng Wee', '+6012-3456789', '2025-11-24 02:55:07'),
(3, 7, 'Muthu Kumar a/l Balakrishnan', '+6013-9876543', '2025-11-24 02:55:07'),
(4, 8, 'Sarah Jenkins', '+44-7700900000', '2025-11-24 02:55:07'),
(5, 9, 'Awang Damit', '+6019-8765432', '2025-11-24 02:55:07'),
(6, 10, 'Naim Najmi', '+601125696678', '2026-01-20 02:05:07'),
(7, 11, 'Naim Najmi', '60377214000', '2026-01-19 03:20:40'),
(8, 12, 'Mazrini Bot', '0193220658', '2026-01-19 03:46:57');

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
(1, 'admin@rentmy.com.my', '$2a$10$DJmkyt/zGU/.FkO8VIZiY.tlGQX/dOTaeex.WyfnZrwfTFhEIK8lO', 'PLATFORM_ADMIN', '/uploads/profiles/profile-1-1767268148259-2bdb3401.png', '2023-01-01 00:00:00', 1),
(2, 'ops@metrocity.com.my', '$2a$10$JZOIVUh.CA5jSEiJLD/sLOU6sQ8LuAheYQc1yUTiLVhj7lEup1iEe', 'FLEET_OWNER', '/uploads/profiles/profile-2-1767269383817-88f30178.png', '2023-02-15 01:30:00', 1),
(3, 'josephine@borneo4x4.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'FLEET_OWNER', '/uploads/profiles/profile-placeholder.png', '2023-03-10 06:20:00', 1),
(4, 'manager@prestigelimo.com', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'FLEET_OWNER', '/uploads/profiles/profile-placeholder.png', '2023-04-05 03:00:00', 1),
(5, 'nurul.izzah@student.um.edu.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', '/uploads/profiles/profile-5-1767270534317-30db6296.jpg', '2024-01-10 02:00:00', 1),
(6, 'jason.lee@corp.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', '/uploads/profiles/profile-placeholder.png', '2024-01-12 08:30:00', 1),
(7, 'muthu.kumar@gmail.com', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', '/uploads/profiles/profile-placeholder.png', '2024-02-01 01:15:00', 1),
(8, 'sarah.jenkins@ukmail.co.uk', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', '/uploads/profiles/profile-placeholder.png', '2024-03-15 05:45:00', 1),
(9, 'awang.damit@sabah.gov.my', '$2a$12$gV3zQhGbeDylo/WoQEBiSejDv1L6jV8n55jWuD5ROXq/ZjnP3NzuS', 'RENTER', '/uploads/profiles/profile-placeholder.png', '2024-04-20 00:30:00', 1),
(10, 'naim.hazre@gmail.com', '$2a$10$BWLW1h60s2t0rfZJMsdoK.gobuE3wnmAfzMzVSYhNxH6OMQsvxoxC', 'RENTER', '/uploads/profiles/profile-10-1768874707122-74e37d1f.jpg', '2026-01-03 03:26:12', 1),
(11, 's70224@ocean.umt.edu.my', '$2a$10$qWjWWZTvUmeAWB/VlYyUXODVyHOw8bOvVjPXAmFMbNNsOf./cxa.6', 'RENTER', '/uploads/profiles/profile-11-1767669609330-406bb356.jpg', '2026-01-06 03:19:07', 1),
(12, 'mazrini.bot@gmail.com', '$2a$10$tlsXNYlG5UxEe.pX8B1dxuABwxJYYoH.klAN4RYP.hsfzAQ9kTEPa', 'RENTER', '/uploads/profiles/profile-12-1768794417390-ae681484.jpg', '2026-01-19 03:34:45', 1);

-- --------------------------------------------------------

--
-- Table structure for table `vehiclemaintenance`
--

CREATE TABLE `vehiclemaintenance` (
  `maintenance_id` bigint(20) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `fleet_owner_id` int(11) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `scheduled_date` date DEFAULT NULL,
  `actual_start_time` datetime DEFAULT NULL,
  `actual_end_time` datetime DEFAULT NULL,
  `estimated_cost` decimal(10,2) DEFAULT NULL,
  `final_cost` decimal(10,2) DEFAULT NULL,
  `current_status` enum('PENDING','IN_PROGRESS','COMPLETED','CANCELLED') DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehiclemaintenance`
--

INSERT INTO `vehiclemaintenance` (`maintenance_id`, `vehicle_id`, `fleet_owner_id`, `description`, `scheduled_date`, `actual_start_time`, `actual_end_time`, `estimated_cost`, `final_cost`, `current_status`, `created_at`) VALUES
(1, 1, 1, '15,000 km service - Engine oil change, oil filter, air filter replacement', '2025-04-20', '2025-04-20 09:00:00', '2025-04-20 15:00:00', 250.00, 268.50, 'COMPLETED', '2025-04-14 18:00:00'),
(2, 1, 1, 'Brake pad replacement (front)', '2025-02-10', '2025-02-10 10:00:00', '2025-02-10 16:00:00', 320.00, 320.00, 'COMPLETED', '2025-02-04 19:00:00'),
(3, 1, 1, 'Scheduled 20,000 km service', '2025-07-15', NULL, NULL, 280.00, NULL, 'PENDING', '2025-06-30 17:00:00'),
(4, 2, 1, 'First 5,000 km service - Oil and filter change, inspection', '2024-11-05', '2024-11-05 09:00:00', '2024-11-05 14:00:00', 180.00, 180.00, 'COMPLETED', '2024-10-31 18:00:00'),
(5, 2, 1, 'Air conditioning system cleaning and servicing', '2024-12-28', '2024-12-28 10:00:00', '2024-12-28 17:00:00', 350.00, 385.00, 'COMPLETED', '2024-12-19 20:00:00'),
(6, 3, 1, '20,000 km major service - Engine oil, transmission fluid, brake fluid top-up', '2024-10-15', '2024-10-15 09:00:00', '2024-10-15 17:00:00', 420.00, 420.00, 'COMPLETED', '2024-10-09 17:00:00'),
(7, 3, 1, 'Replace worn tires (all 4)', '2024-09-20', '2024-09-20 09:00:00', '2024-09-20 16:00:00', 680.00, 680.00, 'COMPLETED', '2024-09-14 18:00:00'),
(8, 3, 1, 'Battery replacement (old battery weak)', '2025-06-08', '2025-06-08 11:00:00', '2025-06-08 13:00:00', 280.00, 280.00, 'COMPLETED', '2025-06-04 22:00:00'),
(9, 4, 2, '30,000 km major service - Full inspection, all fluids changed', '2025-03-10', '2025-03-10 08:00:00', '2025-03-10 18:00:00', 850.00, 925.00, 'COMPLETED', '2025-02-28 17:00:00'),
(10, 4, 2, 'Suspension check and alignment after off-road trip', '2025-05-25', '2025-05-25 09:00:00', '2025-05-25 15:00:00', 380.00, 380.00, 'COMPLETED', '2025-05-19 20:00:00'),
(11, 4, 2, 'Replace cabin air filter and engine air filter', '2025-08-05', NULL, NULL, 150.00, NULL, 'PENDING', '2025-07-31 18:00:00'),
(12, 5, 2, 'Diesel engine 25,000 km service - Oil, diesel filter, inspection', '2025-01-20', '2025-01-20 08:00:00', '2025-01-20 17:00:00', 520.00, 520.00, 'COMPLETED', '2025-01-14 17:00:00'),
(13, 5, 2, '4x4 system check and differential oil change', '2025-04-15', '2025-04-15 09:00:00', '2025-04-15 16:00:00', 680.00, 720.00, 'COMPLETED', '2025-04-09 19:00:00'),
(14, 5, 2, 'Brake system overhaul (front and rear)', '2025-09-10', '2025-09-10 08:00:00', NULL, 950.00, NULL, 'IN_PROGRESS', '2025-09-09 16:30:00'),
(15, 6, 2, '10,000 km service - Oil change and multi-point inspection', '2024-11-25', '2024-11-25 09:00:00', '2024-11-25 15:00:00', 450.00, 450.00, 'COMPLETED', '2024-11-19 18:00:00'),
(16, 6, 2, 'Underbody wash and rust protection treatment', '2025-02-28', '2025-02-28 10:00:00', '2025-02-28 14:00:00', 280.00, 280.00, 'COMPLETED', '2025-02-24 21:00:00'),
(17, 6, 2, 'Scheduled 15,000 km service', '2025-12-20', NULL, NULL, 480.00, NULL, 'PENDING', '2025-12-14 17:00:00'),
(18, 7, 3, 'First 5,000 km service - Complimentary service', '2024-08-10', '2024-08-10 09:00:00', '2024-08-10 14:00:00', 0.00, 0.00, 'COMPLETED', '2024-08-04 17:00:00'),
(19, 7, 3, '10,000 km service - Oil change, filters, brake inspection', '2025-02-18', '2025-02-18 09:00:00', '2025-02-18 15:00:00', 380.00, 380.00, 'COMPLETED', '2025-02-14 18:00:00'),
(20, 7, 3, 'Professional detailing and paint protection', '2025-06-12', '2025-06-12 08:00:00', '2025-06-12 17:00:00', 550.00, 550.00, 'COMPLETED', '2025-06-07 19:00:00'),
(21, 8, 3, '15,000 km premium service - Full synthetic oil, all filters', '2025-03-05', '2025-03-05 08:00:00', '2025-03-05 18:00:00', 780.00, 820.00, 'COMPLETED', '2025-02-28 17:00:00'),
(22, 8, 3, 'Interior deep cleaning and leather conditioning', '2025-01-15', '2025-01-15 09:00:00', '2025-01-15 17:00:00', 650.00, 650.00, 'COMPLETED', '2025-01-09 20:00:00'),
(23, 8, 3, 'Air suspension system check and calibration', '2024-12-10', '2024-12-10 10:00:00', '2024-12-10 16:00:00', 580.00, 620.00, 'COMPLETED', '2024-12-04 18:00:00'),
(24, 9, 3, 'BMW authorized service - 10,000 km interval', '2024-10-20', '2024-10-20 08:00:00', '2024-10-20 17:00:00', 1200.00, 1280.00, 'COMPLETED', '2024-10-14 17:00:00'),
(25, 9, 3, 'Brake pad and disc replacement (premium parts)', '2025-05-08', '2025-05-08 09:00:00', '2025-05-08 16:00:00', 1850.00, 1850.00, 'COMPLETED', '2025-05-02 19:00:00'),
(26, 9, 3, 'Full vehicle inspection and software update', '2025-11-15', NULL, NULL, 450.00, NULL, 'PENDING', '2025-11-09 18:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `vehiclemaintenancelog`
--

CREATE TABLE `vehiclemaintenancelog` (
  `maintenance_log_id` bigint(20) NOT NULL,
  `maintenance_id` bigint(20) NOT NULL,
  `status_value` enum('PENDING','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
  `actor_user_id` int(11) DEFAULT NULL,
  `log_timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `remarks` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehiclemaintenancelog`
--

INSERT INTO `vehiclemaintenancelog` (`maintenance_log_id`, `maintenance_id`, `status_value`, `actor_user_id`, `log_timestamp`, `remarks`) VALUES
(1, 1, 'PENDING', 2, '2025-04-14 18:00:00', 'Maintenance scheduled by fleet owner'),
(2, 1, 'IN_PROGRESS', 2, '2025-04-20 01:00:00', 'Vehicle checked in at workshop'),
(3, 1, 'COMPLETED', 2, '2025-04-20 07:00:00', 'Service completed, additional air filter cleaning RM18.50'),
(4, 2, 'PENDING', 2, '2025-02-04 19:00:00', 'Customer reported squeaking noise'),
(5, 2, 'IN_PROGRESS', 2, '2025-02-10 02:00:00', 'Brake inspection confirmed pad wear'),
(6, 2, 'COMPLETED', 2, '2025-02-10 08:00:00', 'Front brake pads replaced, rear pads 60% remaining'),
(7, 3, 'PENDING', 2, '2025-06-30 17:00:00', 'Scheduled based on projected mileage'),
(8, 4, 'PENDING', 2, '2024-10-31 18:00:00', 'First service due'),
(9, 4, 'COMPLETED', 2, '2024-11-05 06:00:00', 'Complimentary first service completed'),
(10, 5, 'PENDING', 2, '2024-12-19 20:00:00', 'AC not cooling properly after booking'),
(11, 5, 'IN_PROGRESS', 2, '2024-12-28 02:00:00', 'Gas refill and compressor check'),
(12, 5, 'COMPLETED', 2, '2024-12-28 09:00:00', 'AC serviced, gas refilled, extra cleaning RM35'),
(13, 6, 'PENDING', 2, '2024-10-09 17:00:00', 'Major service due'),
(14, 6, 'COMPLETED', 2, '2024-10-15 09:00:00', 'All scheduled maintenance completed'),
(15, 7, 'PENDING', 2, '2024-09-14 18:00:00', 'Tire tread below safety limit'),
(16, 7, 'IN_PROGRESS', 2, '2024-09-20 01:00:00', 'Replacing with Silverstone NS830'),
(17, 7, 'COMPLETED', 2, '2024-09-20 08:00:00', 'All 4 tires replaced and balanced'),
(18, 8, 'PENDING', 2, '2025-06-04 22:00:00', 'Battery voltage low during inspection'),
(19, 8, 'COMPLETED', 2, '2025-06-08 05:00:00', 'Century battery NS60 installed'),
(20, 9, 'PENDING', 3, '2025-02-28 17:00:00', 'Scheduled major service'),
(21, 9, 'IN_PROGRESS', 3, '2025-03-10 00:00:00', 'Full inspection started'),
(22, 9, 'COMPLETED', 3, '2025-03-10 10:00:00', 'Spark plugs also replaced, additional RM75'),
(23, 10, 'PENDING', 3, '2025-05-19 20:00:00', 'Post-booking inspection revealed misalignment'),
(24, 10, 'COMPLETED', 3, '2025-05-25 07:00:00', 'Wheel alignment and balancing done'),
(25, 11, 'PENDING', 3, '2025-07-31 18:00:00', 'Routine filter change scheduled'),
(26, 12, 'PENDING', 3, '2025-01-14 17:00:00', 'Diesel service due'),
(27, 12, 'IN_PROGRESS', 3, '2025-01-20 00:00:00', 'Oil and filter change in progress'),
(28, 12, 'COMPLETED', 3, '2025-01-20 09:00:00', 'Service completed with Mobil Super 3000 5W-30'),
(29, 13, 'PENDING', 3, '2025-04-09 19:00:00', 'Heavy usage requires 4x4 check'),
(30, 13, 'IN_PROGRESS', 3, '2025-04-15 01:00:00', '4WD engagement test and differential service'),
(31, 13, 'COMPLETED', 3, '2025-04-15 08:00:00', 'Additional gear oil added RM40'),
(32, 14, 'PENDING', 3, '2025-09-09 16:30:00', 'Major brake work scheduled'),
(33, 14, 'IN_PROGRESS', 3, '2025-09-10 00:00:00', 'Front brake work started, awaiting rear parts delivery'),
(34, 15, 'PENDING', 3, '2024-11-19 18:00:00', 'Scheduled maintenance'),
(35, 15, 'COMPLETED', 3, '2024-11-25 07:00:00', 'Ford authorized service completed'),
(36, 16, 'PENDING', 3, '2025-02-24 21:00:00', 'Preventive rust protection'),
(37, 16, 'COMPLETED', 3, '2025-02-28 06:00:00', 'Underbody cleaned and coated'),
(38, 17, 'PENDING', 3, '2025-12-14 17:00:00', 'Next scheduled service'),
(39, 18, 'PENDING', 4, '2024-08-04 17:00:00', 'Complimentary service'),
(40, 18, 'COMPLETED', 4, '2024-08-10 06:00:00', 'Honda complimentary first service'),
(41, 19, 'PENDING', 4, '2025-02-14 18:00:00', 'Regular maintenance'),
(42, 19, 'COMPLETED', 4, '2025-02-18 07:00:00', 'Service completed at Honda service center'),
(43, 20, 'PENDING', 4, '2025-06-07 19:00:00', 'Premium detailing for prestige fleet'),
(44, 20, 'IN_PROGRESS', 4, '2025-06-12 00:00:00', 'Paint correction and ceramic coating'),
(45, 20, 'COMPLETED', 4, '2025-06-12 09:00:00', 'Professional detailing completed'),
(46, 21, 'PENDING', 4, '2025-02-28 17:00:00', 'Premium service due'),
(47, 21, 'IN_PROGRESS', 4, '2025-03-05 00:00:00', 'Using premium synthetic oil'),
(48, 21, 'COMPLETED', 4, '2025-03-05 10:00:00', 'Additional cabin filter upgrade RM40'),
(49, 22, 'PENDING', 4, '2025-01-09 20:00:00', 'Monthly deep cleaning'),
(50, 22, 'COMPLETED', 4, '2025-01-15 09:00:00', 'Interior shampooed and leather conditioned'),
(51, 23, 'PENDING', 4, '2024-12-04 18:00:00', 'Air suspension diagnostic'),
(52, 23, 'IN_PROGRESS', 4, '2024-12-10 02:00:00', 'Suspension system inspection'),
(53, 23, 'COMPLETED', 4, '2024-12-10 08:00:00', 'Calibration done, minor leak fixed RM40'),
(54, 24, 'PENDING', 4, '2024-10-14 17:00:00', 'BMW service interval'),
(55, 24, 'IN_PROGRESS', 4, '2024-10-20 00:00:00', 'At BMW authorized service center'),
(56, 24, 'COMPLETED', 4, '2024-10-20 09:00:00', 'Oil service + inspection RM80 extra diagnostic'),
(57, 25, 'PENDING', 4, '2025-05-02 19:00:00', 'Brake warning indicator'),
(58, 25, 'IN_PROGRESS', 4, '2025-05-08 01:00:00', 'Premium Brembo brake parts'),
(59, 25, 'COMPLETED', 4, '2025-05-08 08:00:00', 'Front brake pads and discs replaced'),
(60, 26, 'PENDING', 4, '2025-11-09 18:00:00', 'Scheduled inspection and software update');

-- --------------------------------------------------------

--
-- Table structure for table `vehiclepricehistory`
--

CREATE TABLE `vehiclepricehistory` (
  `price_id` bigint(20) NOT NULL,
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
(9, 9, 900.00, '2023-12-31 16:00:00'),
(10, 10, 120.00, '2025-12-23 06:11:51'),
(11, 11, 150.00, '2025-12-28 09:48:42'),
(20, 10, 130.00, '2025-12-30 10:18:00'),
(22, 12, 135.00, '2025-12-31 04:45:09'),
(23, 12, 145.00, '2025-12-31 04:46:39'),
(24, 12, 205.00, '2026-01-05 04:47:00'),
(25, 13, 230.00, '2026-01-19 06:20:24'),
(26, 14, 245.00, '2026-01-19 06:26:03'),
(27, 13, 250.00, '2026-02-01 01:00:00'),
(28, 1, 150.00, '2026-01-30 02:26:00');

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
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `status` enum('AVAILABLE','MAINTENANCE','RENTED','UNAVAILABLE') DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `deleted_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`vehicle_id`, `fleet_owner_id`, `model`, `brand`, `manufacturing_year`, `registration_no`, `category`, `fuel_type`, `transmission_type`, `mileage`, `vehicle_image_url`, `created_at`, `updated_at`, `status`, `is_deleted`, `deleted_at`) VALUES
(1, 1, 'Myvi 1.5 AV', 'Perodua', 2023, 'VHJ 8821', 'Hatchback', 'Petrol', 'Automatic', 15000, '/uploads/vehicles/vehicle-1-1768873085692-7720c0af.jpg', '2025-11-24 02:58:33', '2026-01-21 02:26:48', 'AVAILABLE', 0, NULL),
(2, 1, 'Bezza 1.3 Advance', 'Perodua', 2024, 'VKE 4402', 'Sedan', 'Petrol', 'Automatic', 5000, '/uploads/vehicles/vehicle-2-1768873099168-fb05c19a.jpg', '2025-11-24 02:58:33', '2026-01-20 01:38:19', 'AVAILABLE', 0, NULL),
(3, 1, 'Saga 1.3 Premium S', 'Proton', 2023, 'BRA 3022', 'Sedan', 'Petrol', 'Automatic', 22000, '/uploads/vehicles/vehicle-3-1768805009652-ae624f8e.webp', '2025-11-24 02:58:33', '2026-01-19 06:43:29', 'AVAILABLE', 0, NULL),
(4, 2, 'X70 1.8 TGDi Premium', 'Proton', 2022, 'SYC 5519', 'SUV', 'Petrol', 'Automatic', 35000, '/uploads/vehicles/vehicle-4-1768873112959-f9bdc510.jpeg', '2025-11-24 02:58:33', '2026-01-20 01:38:33', 'AVAILABLE', 0, NULL),
(5, 2, 'Hilux 2.8 Rogue', 'Toyota', 2023, 'SAB 9921 A', 'Sedan', 'Petrol', 'Automatic', 28000, '/uploads/vehicles/vehicle-5-1768873124536-cefb8aa5.webp', '2025-11-24 02:58:33', '2026-01-20 01:38:44', 'AVAILABLE', 0, NULL),
(6, 2, 'Ranger Wildtrak', 'Ford', 2023, 'QAA 1122', 'Sedan', 'Petrol', 'Automatic', 12000, '/uploads/vehicles/vehicle-6-1768873201421-8e87df7f.png', '2025-11-24 02:58:33', '2026-01-20 01:40:01', 'AVAILABLE', 0, NULL),
(7, 3, 'City 1.5 V Sensing', 'Honda', 2024, 'PQA 8888', 'Sedan', 'Petrol', 'Automatic', 8000, '/uploads/vehicles/vehicle-7-1768873218500-339bba61.png', '2025-11-24 02:58:33', '2026-01-20 01:40:18', 'AVAILABLE', 0, NULL),
(8, 3, 'Vellfire 2.5 ZG', 'Toyota', 2023, 'V 1', 'Sedan', 'Petrol', 'Automatic', 15000, '/uploads/vehicles/vehicle-8-1768873233175-ccc1defc.webp', '2025-11-24 02:58:33', '2026-01-20 01:40:33', 'AVAILABLE', 0, NULL),
(9, 3, '320i M Sport', 'BMW', 2023, 'PPP 77', 'Sedan', 'Petrol', 'Automatic', 9000, '/uploads/vehicles/vehicle-9-1768873246244-a5c21071.png', '2025-11-24 02:58:33', '2026-01-20 01:40:46', 'AVAILABLE', 0, NULL),
(10, 1, 'Camry II', 'Toyota', 2020, 'VEB 8175', 'Sedan', 'Petrol', 'Manual', 23000, '/uploads/vehicles/vehicle_10_a33dd1f2.jpeg', '2025-12-23 06:11:51', '2025-12-28 10:33:25', 'AVAILABLE', 0, '2025-12-28 09:00:53'),
(11, 1, 'X50', 'Proton', 2020, 'RAM 9890', 'SUV', 'Petrol', 'Automatic', 23500, '/uploads/vehicles/vehicle_11_523ead8d.avif', '2025-12-28 09:48:42', '2025-12-29 02:16:11', 'AVAILABLE', 0, NULL),
(12, 1, 'Camry IV', 'Toyota', 2024, 'VBN 4343', 'Sedan', 'Petrol', 'Manual', 24000, '/uploads/vehicles/vehicle_12_c594a63e.jpeg', '2025-12-31 04:45:09', '2025-12-31 04:47:49', 'MAINTENANCE', 1, '2025-12-31 04:53:10'),
(13, 1, 'eMAS', 'Proton', 2025, 'TBU 9242', 'SUV', 'Electric', 'Automatic', 1245, '/uploads/vehicles/vehicle-13-1768803642437-ce1a857a.jpg', '2026-01-19 06:20:24', '2026-01-20 01:33:02', 'UNAVAILABLE', 0, NULL),
(14, 1, 'X90', 'Proton', 2024, 'TGH 3224', 'SUV', 'Petrol', 'Automatic', 7500, '/uploads/vehicles/vehicle-1768803963782-1768803963784-b8133739.webp', '2026-01-19 06:26:03', '2026-01-19 06:26:03', 'UNAVAILABLE', 0, NULL);

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
  ADD KEY `fleet_owner_id` (`fleet_owner_id`),
  ADD KEY `idx_emergency_maintenance_id` (`maintenance_id`);

--
-- Indexes for table `vehiclemaintenancelog`
--
ALTER TABLE `vehiclemaintenancelog`
  ADD PRIMARY KEY (`maintenance_log_id`),
  ADD KEY `idx_maintenance_log_ref` (`maintenance_id`);

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
  MODIFY `address_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bookingstatuslog`
--
ALTER TABLE `bookingstatuslog`
  MODIFY `booking_log_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

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
  MODIFY `payment_log_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `platformadmins`
--
ALTER TABLE `platformadmins`
  MODIFY `admin_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `renters`
--
ALTER TABLE `renters`
  MODIFY `renter_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `vehiclemaintenance`
--
ALTER TABLE `vehiclemaintenance`
  MODIFY `maintenance_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `vehiclemaintenancelog`
--
ALTER TABLE `vehiclemaintenancelog`
  MODIFY `maintenance_log_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- AUTO_INCREMENT for table `vehiclepricehistory`
--
ALTER TABLE `vehiclepricehistory`
  MODIFY `price_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `vehicle_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

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
-- Constraints for table `vehiclemaintenancelog`
--
ALTER TABLE `vehiclemaintenancelog`
  ADD CONSTRAINT `fk_maintenancelog_main` FOREIGN KEY (`maintenance_id`) REFERENCES `vehiclemaintenance` (`maintenance_id`) ON DELETE CASCADE;

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
