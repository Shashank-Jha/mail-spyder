package com.spyder.smtp.utils;

import com.spyder.smtp.entity.EmailData;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static void init(String url, String user, String pass) {
        if (sessionFactory != null) return;

        try {
            Map<String, Object> settings = new HashMap<>();
            settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
            settings.put(Environment.URL, url);
            settings.put(Environment.USER, user);
            settings.put(Environment.PASS, pass);
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
            settings.put(Environment.HBM2DDL_AUTO, "update");
            settings.put(Environment.SHOW_SQL, "true");
            settings.put(Environment.FORMAT_SQL, "true");
            settings.put("hibernate.jdbc.time_zone", "Asia/Kolkata");

            // 🔹 Build registry
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(settings)
                    .build();

            // 🔹 Add annotated entity class
            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(EmailData.class);

            // 🔹 Build metadata
            Metadata metadata = sources.buildMetadata();

            // 🔹 Build session factory (this also prepares schema)
            sessionFactory = metadata.buildSessionFactory();

            // 🔹 Force Hibernate to trigger schema update immediately
            sessionFactory.openSession().close();

            System.out.println("✅ Hibernate initialized and schema updated.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Hibernate initialization failed: " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) sessionFactory.close();
    }
}
