package org.example.poll_craft.service;

import lombok.RequiredArgsConstructor;
import org.example.poll_craft.model.*;
import org.example.poll_craft.repository.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final AnswerRepository answerRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public boolean hasVoted(UUID pollId, String ipAddress, String sessionId, UserDetails userDetails) {
        // Проверка по IP и сессии
        Optional<Vote> existingVote = voteRepository.findByPollIdAndIpAddressAndSessionId(
                pollId, ipAddress, sessionId
        );

        if (existingVote.isPresent()) {
            return true;
        }

        // Если пользователь авторизован, проверяем по user_id
        if (userDetails != null) {
            User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (user != null) {
                return voteRepository.existsByPollIdAndUserId(pollId, user.getId());
            }
        }

        return false;
    }

    @Transactional
    public void submitVote(
            UUID pollId, Map<String, String> answers,
            String ipAddress, String sessionId,
            UserDetails userDetails
    ) {
        if (hasVoted(pollId, ipAddress, sessionId, userDetails)) {
            throw new IllegalStateException("Вы уже голосовали в этом опросе");
        }

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));

        if (!poll.getIsActive()) {
            throw new IllegalStateException("Этот опрос уже закрыт");
        }

        Vote vote = Vote.builder()
                .poll(poll)
                .ipAddress(ipAddress)
                .sessionId(sessionId)
                .build();

        if (userDetails != null) {
            User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            vote.setUser(user);
        }

        vote = voteRepository.save(vote);

        // Сохраняем ответы
        for (Question question : poll.getQuestions()) {
            String answerKey = "question_" + question.getId();
            String answerValue = answers.get(answerKey);

            if (answerValue == null || answerValue.trim().isEmpty()) {
                if (question.getRequired()) {
                    throw new IllegalStateException("Ответ на вопрос обязателен: " + question.getText());
                }
                continue;
            }

            Answer answer = Answer.builder()
                    .vote(vote)
                    .question(question)
                    .build();

            if (question.getType() == QuestionType.TEXT) {
                answer.setTextAnswer(answerValue);
            } else {
                // Для SINGLE/MULTIPLE
                if (question.getType() == QuestionType.SINGLE) {
                    Option option = findOptionById(Long.parseLong(answerValue));
                    answer.setOption(option);
                } else { // MULTIPLE
                    String[] optionIds = answerValue.split(",");
                    for (String optionId : optionIds) {
                        Answer multiAnswer = Answer.builder()
                                .vote(vote)
                                .question(question)
                                .option(findOptionById(Long.parseLong(optionId)))
                                .build();
                        answerRepository.save(multiAnswer);
                    }
                    continue;
                }
            }
            answerRepository.save(answer);
        }
    }

    public List<Map<String, Object>> getResults(UUID pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));

        List<Map<String, Object>> results = new ArrayList<>();
        long totalVotes = voteRepository.countByPollId(pollId);

        for (Question question : poll.getQuestions()) {
            Map<String, Object> qResult = new HashMap<>();
            qResult.put("questionId", question.getId());
            qResult.put("text", question.getText());
            qResult.put("type", question.getType().name());

            if (question.getType() == QuestionType.TEXT) {
                List<String> textAnswers = answerRepository.findTextAnswersByQuestionId(question.getId());
                qResult.put("answers", textAnswers);
            } else {
                List<Map<String, Object>> optionsStats = new ArrayList<>();
                for (Option option : question.getOptions()) {
                    Map<String, Object> optStat = new HashMap<>();
                    optStat.put("optionId", option.getId());
                    optStat.put("text", option.getText());

                    long count = answerRepository.countByQuestionIdAndOptionId(question.getId(), option.getId());
                    optStat.put("count", count);

                    double percentage = totalVotes > 0 ? (count * 100.0 / totalVotes) : 0;
                    optStat.put("percentage", Math.round(percentage * 10) / 10.0);

                    optionsStats.add(optStat);
                }
                qResult.put("options", optionsStats);
            }

            results.add(qResult);
        }

        return results;
    }

    private Option findOptionById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
    }

}
