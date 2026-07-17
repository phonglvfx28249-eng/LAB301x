package com.windle.blockchaintrading.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public String login() {
        // Handle login logic here
        return "redirect:/dashboard"; // Redirect to dashboard after successful login
    }

    @PostMapping("/register")
    public String register() {
        // Handle registration logic here
        return "redirect:/login"; // Redirect to login page after successful registration
    }
}
