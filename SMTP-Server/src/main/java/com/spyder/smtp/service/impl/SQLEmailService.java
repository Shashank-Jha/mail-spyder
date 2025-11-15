package com.spyder.smtp.service.impl;

import com.spyder.smtp.entity.EmailData;
import com.spyder.smtp.service.EmailService;
import com.spyder.smtp.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SQLEmailService implements EmailService {

    @Override
    public void saveEmail(EmailData data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(data);
            tx.commit();
            System.out.println("✅ Email saved via Hibernate: " + data.getRecipient());
        } catch (Exception e) {
            System.err.println("❌ Failed to save email with Hibernate");
            e.printStackTrace();
        }
    }

    @Override
    public boolean isValidEmailId(String emailId) {
        try {
            String email = sanitizeEmail(emailId);
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(
                            URI.create("http://mailspyder-api:8080/users/exists/" + encodedEmail))
                    .header("accept", "application/json")
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("API returned non-200 status: " + response.statusCode());
                return false;
            }
            var json = response.body().trim();
            System.out.println("reponse json :: "+json);
            return json.contains("\"exists\":true");
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private static String sanitizeEmail(String email) {
        if (email == null) return null;

        // 1. Trim whitespace (spaces, tabs, CRLF junk)
        email = email.trim();

        // 2. Remove all invisible / non-printable / control chars
        // (ZERO WIDTH SPACE, WORD JOINER U+2060, etc.)
        email = email.replaceAll("[\\u200B-\\u200D\\uFEFF\\u2060]", "");

        // 3. Remove EVERYTHING not valid for email
        // Allows: letters, digits, @ . _ - +
        email = email.replaceAll("[^A-Za-z0-9@._+\\-]", "");

        return email;
    }
}
