-- Flyway Migration: V2__create_roles_table.sql

CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    created_at  TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    description CHARACTER VARYING(255),
    name        CHARACTER VARYING(50) NOT NULL
);

-- Insert default role
INSERT INTO roles (created_at, description, name)
VALUES (NOW(), 'default user', 'ROLE_USER');