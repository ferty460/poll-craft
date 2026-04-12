package org.example.poll_craft.service;

import lombok.RequiredArgsConstructor;
import org.example.poll_craft.model.*;
import org.example.poll_craft.model.dto.CreatePollRequest;
import org.example.poll_craft.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public Poll createPoll(CreatePollRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Poll poll = Poll.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .isActive(true)
                .build();

        poll = pollRepository.save(poll);

        int questionOrder = 0;
        for (CreatePollRequest.QuestionDto qDto : request.getQuestions()) {
            Question question = Question.builder()
                    .poll(poll)
                    .text(qDto.getText())
                    .type(QuestionType.valueOf(qDto.getType()))
                    .required(qDto.getRequired() != null ? qDto.getRequired() : true)
                    .displayOrder(questionOrder++)
                    .build();

            question = questionRepository.save(question);

            // Сохраняем варианты ответов для SINGLE/MULTIPLE
            if (qDto.getOptions() != null && !qDto.getOptions().isEmpty()) {
                int optionOrder = 0;
                for (CreatePollRequest.QuestionDto.OptionDto oDto : qDto.getOptions()) {
                    Option option = Option.builder()
                            .question(question)
                            .text(oDto.getText())
                            .displayOrder(optionOrder++)
                            .build();
                    optionRepository.save(option);
                }
            }
        }

        return poll;
    }

    public Poll getPoll(UUID id) {
        return pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Poll not found"));
    }

    public List<Poll> getUserPolls(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return pollRepository.findByUser(user);
    }

    @Transactional
    public void deletePoll(UUID id, String userEmail) {
        Poll poll = getPoll(id);

        if (!poll.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You don't have permission to delete this poll");
        }

        for (Question question : poll.getQuestions()) {
            answerRepository.deleteByQuestionId(question.getId());
        }

        voteRepository.deleteByPollId(id);

        for (Question question : poll.getQuestions()) {
            optionRepository.deleteByQuestionId(question.getId());
        }

        questionRepository.deleteByPollId(id);
        pollRepository.delete(poll);
    }

    @Transactional
    public void togglePollStatus(UUID id, String userEmail) {
        Poll poll = getPoll(id);
        if (!poll.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You don't have permission to modify this poll");
        }
        poll.setIsActive(!poll.getIsActive());
        pollRepository.save(poll);
    }

}
