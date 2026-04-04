-- ============================================================
-- FleetShare Migration Fix Script
-- Run this against the `fleetshareplayground` database
-- Generated: 2026-04-04
-- ============================================================

USE fleetshareplayground;

-- ------------------------------------------------------------
-- 1. Add missing columns to `vehiclemaintenance`
-- ------------------------------------------------------------
ALTER TABLE vehiclemaintenance
  ADD COLUMN IF NOT EXISTS maintenance_type   VARCHAR(100)     NULL,
  ADD COLUMN IF NOT EXISTS notes              VARCHAR(2000)    NULL,
  ADD COLUMN IF NOT EXISTS service_center_name VARCHAR(200)   NULL,
  ADD COLUMN IF NOT EXISTS service_provider_id BIGINT         NULL,
  ADD COLUMN IF NOT EXISTS warranty_applicable TINYINT(1)     NOT NULL DEFAULT 0;

-- ------------------------------------------------------------
-- 2. Create missing `service_providers` table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS service_providers (
  provider_id      BIGINT       NOT NULL AUTO_INCREMENT,
  fleet_owner_id   BIGINT       NOT NULL,
  provider_name    VARCHAR(200) NOT NULL,
  contact_person   VARCHAR(100) NULL,
  phone            VARCHAR(20)  NULL,
  email            VARCHAR(100) NULL,
  address          VARCHAR(500) NULL,
  specialty        VARCHAR(100) NULL,
  rating           DECIMAL(3,2) NULL,
  total_jobs       INT          NULL DEFAULT 0,
  notes            VARCHAR(1000) NULL,
  is_active        TINYINT(1)   NULL DEFAULT 1,
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (provider_id),
  KEY idx_sp_fleet_owner (fleet_owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
