CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    poll_id UUID NOT NULL,
    text TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    display_order INT DEFAULT 0,
    required BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_questions_poll FOREIGN KEY (poll_id) REFERENCES polls(id) ON DELETE CASCADE,
    CONSTRAINT chk_question_type CHECK (type IN ('SINGLE', 'MULTIPLE', 'TEXT'))
);

COMMENT ON TABLE questions IS 'Таблица вопросов опросов';
COMMENT ON COLUMN questions.id IS 'Уникальный идентификатор вопроса';
COMMENT ON COLUMN questions.poll_id IS 'ID опроса, к которому относится вопрос';
COMMENT ON COLUMN questions.text IS 'Текст вопроса';
COMMENT ON COLUMN questions.type IS 'Тип вопроса (SINGLE, MULTIPLE, TEXT)';
COMMENT ON COLUMN questions.display_order IS 'Порядок отображения вопроса';
COMMENT ON COLUMN questions.required IS 'Обязательность ответа на вопрос';

CREATE INDEX idx_questions_poll_id ON questions(poll_id);
CREATE INDEX idx_questions_type ON questions(type);
CREATE INDEX idx_questions_display_order ON questions(poll_id, display_order);