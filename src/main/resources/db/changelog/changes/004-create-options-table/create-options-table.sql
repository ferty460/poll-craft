
CREATE TABLE IF NOT EXISTS options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    text VARCHAR(255) NOT NULL,
    display_order INT DEFAULT 0,
    CONSTRAINT fk_options_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

COMMENT ON TABLE options IS 'Таблица вариантов ответов для вопросов с выбором';
COMMENT ON COLUMN options.id IS 'Уникальный идентификатор варианта ответа';
COMMENT ON COLUMN options.question_id IS 'ID вопроса, к которому относится вариант';
COMMENT ON COLUMN options.text IS 'Текст варианта ответа';
COMMENT ON COLUMN options.display_order IS 'Порядок отображения варианта';

CREATE INDEX idx_options_question_id ON options(question_id);
CREATE INDEX idx_options_display_order ON options(question_id, display_order);