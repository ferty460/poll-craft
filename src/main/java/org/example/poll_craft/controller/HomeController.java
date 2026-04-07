package org.example.poll_craft.controller;

import org.example.poll_craft.model.User;
import org.example.poll_craft.model.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal != null) {
            User user = principal.user();
            model.addAttribute("username", user.getUsername());
        }

        return "index";
    }

}
