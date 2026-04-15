CREATE TABLE IF NOT EXISTS votes (
    id BIGSERIAL PRIMARY KEY,
    poll_id UUID NOT NULL,
    user_id BIGINT,
    ip_address VARCHAR(45) NOT NULL,
    session_id VARCHAR(255),
    CONSTRAINT fk_votes_poll FOREIGN KEY (poll_id) REFERENCES polls(id) ON DELETE CASCADE,
    CONSTRAINT fk_votes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uk_votes_poll_ip_session UNIQUE (poll_id, ip_address, session_id)
);

COMMENT ON TABLE votes IS 'Таблица голосований';
COMMENT ON COLUMN votes.id IS 'Уникальный идентификатор голосования';
COMMENT ON COLUMN votes.poll_id IS 'ID опроса, в котором проголосовали';
COMMENT ON COLUMN votes.user_id IS 'ID пользователя (если авторизован)';
COMMENT ON COLUMN votes.ip_address IS 'IP-адрес голосующего';
COMMENT ON COLUMN votes.session_id IS 'ID сессии голосующего';

CREATE INDEX idx_votes_poll_id ON votes(poll_id);
CREATE INDEX idx_votes_user_id ON votes(user_id);
CREATE INDEX idx_votes_ip_address ON votes(ip_address);
CREATE INDEX idx_votes_poll_ip ON votes(poll_id, ip_address);