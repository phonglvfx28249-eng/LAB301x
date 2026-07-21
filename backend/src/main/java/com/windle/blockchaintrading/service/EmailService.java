package com.windle.blockchaintrading.service;

public interface EmailService {

    public void sendEmailResetPassword(String to, String subject, String body);
}
