-- ============================================================
-- FleetShare Central Payout Module — Database Migration
-- ============================================================
-- Run this script manually against your MySQL database
-- since spring.jpa.hibernate.ddl-auto=none
-- ============================================================

-- 1. Add toyyibpay_username column to fleetowners table
ALTER TABLE fleetowners
ADD COLUMN toyyibpay_username VARCHAR(100) DEFAULT NULL
COMMENT 'Owner toyyibPay username for split payment routing';

-- 2. Add commission tracking columns to payments table
ALTER TABLE payments
ADD COLUMN platform_commission DECIMAL(10,2) DEFAULT NULL
    COMMENT 'Platform commission amount for this payment',
ADD COLUMN owner_payout DECIMAL(10,2) DEFAULT NULL
    COMMENT 'Amount routed to fleet owner for this payment',
ADD COLUMN commission_rate DECIMAL(5,4) DEFAULT NULL
    COMMENT 'Commission rate applied (e.g., 0.1000 = 10%)',
ADD COLUMN split_payment_enabled TINYINT(1) DEFAULT 0
    COMMENT '1 if split payment was used, 0 otherwise';

-- 3. Create platform_config table for dynamic commission management
CREATE TABLE IF NOT EXISTS platform_config (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value VARCHAR(500) NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Insert default platform configuration values
INSERT INTO platform_config (config_key, config_value, description) VALUES
('commission_rate', '0.10', 'Platform commission rate (10%)'),
('payout_mode', 'SPLIT', 'SPLIT = ToyyibPay split, MANUAL = manual transfer')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);
