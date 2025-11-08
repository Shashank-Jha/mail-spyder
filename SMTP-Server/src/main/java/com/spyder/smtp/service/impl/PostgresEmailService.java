package com.spyder.smtp.service.impl;

import com.spyder.smtp.entity.EmailData;
import com.spyder.smtp.service.EmailService;
import com.spyder.smtp.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PostgresEmailService implements EmailService {

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
    public List<EmailData> fetchAllByEmailId(String emailId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM EmailData e WHERE e.recipient LIKE :email";
            Query<EmailData> query = session.createQuery(hql, EmailData.class);
            query.setParameter("email", "%" + emailId + "%");
            return query.list();
        } catch (Exception e) {
            System.err.println("❌ Failed to fetch emails via Hibernate");
            e.printStackTrace();
            return List.of();
        }
    }
}
