CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

COMMENT ON TABLE users IS 'Таблица пользователей';
COMMENT ON COLUMN users.id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN users.username IS 'Имя пользователя';
COMMENT ON COLUMN users.email IS 'Email пользователя (уникальный)';
COMMENT ON COLUMN users.password IS 'Хэш пароля';

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);