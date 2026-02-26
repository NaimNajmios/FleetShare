-- Add payment QR code and bank information fields to fleetowners table
ALTER TABLE fleetowners
    ADD COLUMN payment_qr_url VARCHAR(1024) DEFAULT NULL,
    ADD COLUMN bank_name VARCHAR(100) DEFAULT NULL,
    ADD COLUMN bank_account_number VARCHAR(50) DEFAULT NULL,
    ADD COLUMN bank_account_holder VARCHAR(100) DEFAULT NULL,
    ADD COLUMN toyyibpay_secret_key VARCHAR(255) DEFAULT NULL,
    ADD COLUMN toyyibpay_category_code VARCHAR(100) DEFAULT NULL;
