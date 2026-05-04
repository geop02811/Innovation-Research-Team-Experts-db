CREATE TABLE grants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funder VARCHAR(512) NOT NULL,
    title VARCHAR(1024) NOT NULL,
    description TEXT,
    amount VARCHAR(255),
    closing_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    category VARCHAR(255),
    apply_url VARCHAR(1024),
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
