package com.spyder.smtp.scheduler;

import com.spyder.smtp.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EmailPrunScheduler implements Runnable {

    @Override
    public void run() {
        System.out.println("🧹 Cleanup task triggered...");
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int deletedCount = session.createMutationQuery(
                    "DELETE FROM emails e WHERE e.receivedAt < CURRENT_TIMESTAMP - 7"
            ).executeUpdate();

            tx.commit();

            System.out.println("🧹 Cleanup complete: " + deletedCount + " old emails deleted.");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("❌ Cleanup failed: " + e.getMessage());
        }
    }
}
