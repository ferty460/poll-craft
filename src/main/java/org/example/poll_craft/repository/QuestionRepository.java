package org.example.poll_craft.repository;

import org.example.poll_craft.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    void deleteByPollId(UUID pollId);

}
