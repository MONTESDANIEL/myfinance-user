CREATE DATABASE user_db;

USE user_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Campo id, auto incremental
    id_type VARCHAR(255) NOT NULL,            -- Tipo de ID (por ejemplo, 'CC', 'CE', 'NIT', 'PAS', 'NIE', 'RUT', 'CEX')
    name VARCHAR(255) NOT NULL,              -- Nombre del usuario
    email VARCHAR(255) UNIQUE NOT NULL,      -- Correo electrónico único
    phone_number BIGINT,                    -- Número de teléfono
    birth_date DATE,                         -- Fecha de nacimiento
    password VARCHAR(255) NOT NULL           -- Contraseña del usuario
);