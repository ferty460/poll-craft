package org.example.poll_craft.repository;

import org.example.poll_craft.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

    void deleteByQuestionId(Long questionId);

}
