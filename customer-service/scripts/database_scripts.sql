CREATE ROLE myapp_user WITH LOGIN PASSWORD 'securepassword';

CREATE DATABASE myapp_db;

GRANT ALL PRIVILEGES ON DATABASE myapp_db TO myapp_user;

GRANT ALL PRIVILEGES ON SCHEMA public TO your_user;

CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    date_of_birth TIMESTAMPTZ,
    gender VARCHAR(50),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);