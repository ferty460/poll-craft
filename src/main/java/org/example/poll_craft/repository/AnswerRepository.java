package org.example.poll_craft.repository;

import org.example.poll_craft.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a.textAnswer FROM Answer a WHERE a.question.id = :questionId AND a.textAnswer IS NOT NULL")
    List<String> findTextAnswersByQuestionId(Long questionId);

    long countByQuestionIdAndOptionId(Long questionId, Long optionId);

    void deleteByQuestionId(Long questionId);

}
