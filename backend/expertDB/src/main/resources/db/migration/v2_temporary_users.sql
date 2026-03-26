CREATE TABLE IF NOT EXISTS temporary_users (
    id UUID PRIMARY KEY,
    title_prefix VARCHAR(10),
    full_name VARCHAR(255) NOT NULL,
    academic_rank VARCHAR(50),
    university_email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50),
    highest_qualification TEXT,
    professional_memberships TEXT,
    compliance_accreditation TEXT,
    faculty VARCHAR(255),
    department VARCHAR(100),
    years_consultancy_experience VARCHAR(50),
    consultancy_experience TEXT,
    consultancy_availability VARCHAR(50),
    preferred_consultancy_types TEXT,
    geographic_scope VARCHAR(50),
    skills_competences TEXT,
    languages_spoken TEXT,
    areas_of_expertise TEXT,
    industrial_areas_expertise TEXT,
    notes TEXT,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_temporary_users_email ON temporary_users(university_email);
CREATE INDEX idx_temporary_users_status ON temporary_users(status);