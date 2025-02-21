-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

-- Create locations table
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(255),
    area VARCHAR(100),
    city VARCHAR(100),
    province VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true
);

-- Create incidents table
CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    reporter_id BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    verification_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

-- Create comments table
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    incident_id BIGINT NOT NULL REFERENCES incidents(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

-- Create votes table
CREATE TABLE votes (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    vote_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (incident_id) REFERENCES incidents(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_vote (incident_id, user_id)
);

-- Create ratings table
CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    incident_id BIGINT NOT NULL REFERENCES incidents(id),
    value INTEGER NOT NULL CHECK (value >= 1 AND value <= 5),
    comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    UNIQUE(incident_id, user_id)
);

-- Create indexes
CREATE INDEX idx_incidents_location ON incidents(location_id);
CREATE INDEX idx_incidents_reporter ON incidents(reporter_id);
CREATE INDEX idx_comments_incident ON comments(incident_id);
CREATE INDEX idx_comments_user ON comments(user_id);
CREATE INDEX idx_votes_incident ON votes(incident_id);
CREATE INDEX idx_votes_user ON votes(user_id);
CREATE INDEX idx_ratings_incident ON ratings(incident_id);
CREATE INDEX idx_ratings_user ON ratings(user_id);
