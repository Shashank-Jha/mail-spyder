package com.spyder.smtp.service.impl;

import com.spyder.smtp.entity.EmailData;
import com.spyder.smtp.service.EmailService;
import com.spyder.smtp.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

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
}
