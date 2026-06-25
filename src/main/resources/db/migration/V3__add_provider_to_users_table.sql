-- Flyway Migration: V3__add_provider_to_users_table.sql

ALTER TABLE users
ADD COLUMN provider CHARACTER VARYING(50) NOT NULL DEFAULT 'LOCAL';