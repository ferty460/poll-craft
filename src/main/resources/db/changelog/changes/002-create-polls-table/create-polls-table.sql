CREATE TABLE IF NOT EXISTS polls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_polls_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

COMMENT ON TABLE polls IS 'Таблица опросов';
COMMENT ON COLUMN polls.id IS 'Уникальный идентификатор опроса (UUID)';
COMMENT ON COLUMN polls.user_id IS 'ID пользователя-создателя опроса';
COMMENT ON COLUMN polls.title IS 'Название опроса';
COMMENT ON COLUMN polls.description IS 'Описание опроса';
COMMENT ON COLUMN polls.is_active IS 'Статус опроса (активен/закрыт)';

CREATE INDEX idx_polls_user_id ON polls(user_id);
CREATE INDEX idx_polls_is_active ON polls(is_active);