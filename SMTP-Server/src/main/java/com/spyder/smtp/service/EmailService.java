package com.spyder.smtp.service;

import com.spyder.smtp.entity.EmailData;

public interface EmailService {
    public void saveEmail(EmailData emailData);
    public boolean isValidEmailId(String emailId);
}
