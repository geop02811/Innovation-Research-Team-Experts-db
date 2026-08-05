ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT true;

CREATE TABLE signup_otps (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_signup_otps_email_active
    ON signup_otps (email, expires_at DESC)
    WHERE used_at IS NULL;
