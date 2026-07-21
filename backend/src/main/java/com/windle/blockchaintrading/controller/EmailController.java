package com.windle.blockchaintrading.controller;


import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.service.EmailService;
import com.windle.blockchaintrading.service.UserService;
import com.windle.blockchaintrading.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class EmailController {

    private EmailService emailService;
    private UserService userService;

    @Autowired
    public EmailController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }


    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@RequestBody String email) {
        if(!userService.isEmailTaken(email)) {
            return ResponseEntity.badRequest().build();
        }

        try{
            String newPassword = PasswordUtil.generateRandomString(10);
            emailService.sendEmailResetPassword(email, "Reset Password", newPassword);

            User user = userService.getUserByEmail(email);
            userService.updatePassword(newPassword, user.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok().build();
    }
}
