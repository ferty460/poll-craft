package org.example.poll_craft.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poll_craft.model.Poll;
import org.example.poll_craft.model.Question;
import org.example.poll_craft.model.QuestionType;
import org.example.poll_craft.model.UserPrincipal;
import org.example.poll_craft.model.dto.CreatePollRequest;
import org.example.poll_craft.repository.VoteRepository;
import org.example.poll_craft.service.PollService;
import org.example.poll_craft.service.VoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/polls")
public class PollController {

    private final PollService pollService;
    private final VoteService voteService;
    private final VoteRepository voteRepository;

    @GetMapping("/create")
    public String createPollForm(Model model, @AuthenticationPrincipal UserPrincipal principal) {
        CreatePollRequest request = CreatePollRequest.builder()
                .title("")
                .description("")
                .questions(new ArrayList<>())
                .build();

        CreatePollRequest.QuestionDto defaultQuestion = CreatePollRequest.QuestionDto.builder()
                .text("")
                .type("SINGLE")
                .required(true)
                .options(new ArrayList<>())
                .build();

        defaultQuestion.getOptions().add(CreatePollRequest.QuestionDto.OptionDto.builder().text("").build());
        defaultQuestion.getOptions().add(CreatePollRequest.QuestionDto.OptionDto.builder().text("").build());

        request.getQuestions().add(defaultQuestion);

        if (principal != null) {
            model.addAttribute("username", principal.user().getUsername());
        }
        model.addAttribute("pollRequest", request);

        return "poll/create";
    }

    @PostMapping("/create")
    public String createPoll(
            @Valid @ModelAttribute("pollRequest") CreatePollRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "poll/create";
        }

        Poll poll = pollService.createPoll(request, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("success", "Опрос успешно создан!");
        return "redirect:/polls/" + poll.getId();
    }

    @GetMapping("/{id}")
    public String viewPoll(
            @PathVariable UUID id,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model
    ) {
        try {
            Poll poll = pollService.getPoll(id);
            String ipAddress = getClientIp(request);
            String sessionId = request.getSession().getId();
            boolean hasVoted = voteService.hasVoted(id, ipAddress, sessionId, principal);

            model.addAttribute("poll", poll);
            model.addAttribute("hasVoted", hasVoted);
            if (principal != null) {
                model.addAttribute("username", principal.user().getUsername());
            }

            return "poll/view";
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Опрос с ID " + id + " не найден");
        }
    }

    @GetMapping("/{id}/results")
    public String viewResults(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal, Model model) {
        Poll poll = pollService.getPoll(id);
        List<Map<String, Object>> results = voteService.getResults(id);
        long totalVotes = voteRepository.countByPollId(id);

        if (principal != null) {
            model.addAttribute("username", principal.user().getUsername());
        }
        model.addAttribute("poll", poll);
        model.addAttribute("results", results);
        model.addAttribute("totalVotes", totalVotes);

        return "poll/results";
    }

    @PostMapping("/{id}/vote")
    public String submitVote(
            @PathVariable UUID id,
            @RequestParam Map<String, String> allParams,
            HttpServletRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            RedirectAttributes redirectAttributes
    ) {
        Map<String, String> answers = new HashMap<>();

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                answers.put(entry.getKey(), entry.getValue());
            }
        }

        String ipAddress = getClientIp(request);
        String sessionId = request.getSession().getId();

        try {
            voteService.submitVote(id, answers, ipAddress, sessionId, principal);
            redirectAttributes.addFlashAttribute("success", "Ваш голос учтён!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/polls/" + id;
        }

        return "redirect:/polls/" + id + "/results";
    }

    @GetMapping("/my")
    public String myPolls(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<Poll> polls = pollService.getUserPolls(principal.getUsername());

        for (Poll poll : polls) {
            long totalVotes = voteRepository.countByPollId(poll.getId());
            poll.setTotalVotes(totalVotes);
        }

        model.addAttribute("username", principal.user().getUsername());
        model.addAttribute("polls", polls);

        return "poll/my-polls";
    }

    @PostMapping("/{id}/delete")
    public String deletePoll(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            pollService.deletePoll(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("deleted", "true");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/polls/my";
    }

    @PostMapping("/{id}/toggle")
    public String togglePoll(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            pollService.togglePollStatus(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("toggled", "true");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/polls/my";
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

}
