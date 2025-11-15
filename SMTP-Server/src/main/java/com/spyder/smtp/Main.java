package com.spyder.smtp;

import com.spyder.smtp.handler.SMTPHandler;
import com.spyder.smtp.scheduler.EmailPrunScheduler;
import com.spyder.smtp.service.EmailService;
import com.spyder.smtp.service.impl.PostgresEmailService;
import com.spyder.smtp.utils.HibernateUtil;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPass = dotenv.get("DB_PASSWORD");
        // Initialize Hibernate once
        HibernateUtil.init(dbUrl, dbUser, dbPass);

        //Email table Pruning every week
        startCleanupScheduler();

        EmailService emailService = new SQLEmailService();
        try (ServerSocket serverSocket = new ServerSocket(25, 50, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("SMTP Server running on port: 25");
            System.out.println(dbUrl + "->" + dbUser + "->" + dbPass);

            while (true) {
                Socket client = serverSocket.accept();
                new Thread(new SMTPHandler(client, emailService)).start();
            }
        }


    }

    private static void startCleanupScheduler() {
        System.out.println("🕓 Initializing cleanup scheduler...");
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                new EmailPrunScheduler(),
                24,              // initial delay
                1,             // repeat interval
                TimeUnit.HOURS  // unit
        );

        System.out.println("🕓 Cleanup scheduler started (runs every 24h)");
    }

}