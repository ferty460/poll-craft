package org.example.poll_craft.repository;

import org.example.poll_craft.model.Poll;
import org.example.poll_craft.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {

    List<Poll> findByUser(User user);

}
