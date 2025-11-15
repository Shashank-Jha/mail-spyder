package com.spyder.smtp.service;

import com.spyder.smtp.entity.EmailData;

import java.util.List;

public interface EmailService {
    public void saveEmail(EmailData emailData);
}
