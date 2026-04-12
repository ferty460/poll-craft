package org.example.poll_craft.repository;

import org.example.poll_craft.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByPollIdAndIpAddressAndSessionId(UUID pollId, String ip, String sessionId);

    boolean existsByPollIdAndUserId(UUID pollId, Long userId);

    long countByPollId(UUID pollId);

    void deleteByPollId(UUID pollId);

}
