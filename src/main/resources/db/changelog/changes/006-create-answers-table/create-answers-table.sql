CREATE TABLE IF NOT EXISTS answers (
    id BIGSERIAL PRIMARY KEY,
    vote_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    option_id BIGINT,
    text_answer TEXT,
    CONSTRAINT fk_answers_vote FOREIGN KEY (vote_id) REFERENCES votes(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_option FOREIGN KEY (option_id) REFERENCES options(id) ON DELETE CASCADE
);

COMMENT ON TABLE answers IS 'Таблица ответов на вопросы';
COMMENT ON COLUMN answers.id IS 'Уникальный идентификатор ответа';
COMMENT ON COLUMN answers.vote_id IS 'ID голосования';
COMMENT ON COLUMN answers.question_id IS 'ID вопроса';
COMMENT ON COLUMN answers.option_id IS 'ID выбранного варианта (для SINGLE/MULTIPLE)';
COMMENT ON COLUMN answers.text_answer IS 'Текстовый ответ (для TEXT вопроса)';

CREATE INDEX idx_answers_vote_id ON answers(vote_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_answers_option_id ON answers(option_id);
CREATE INDEX idx_answers_vote_question ON answers(vote_id, question_id);