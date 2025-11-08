package com.spyder.smtp.handler;

import com.spyder.smtp.entity.EmailData;
import com.spyder.smtp.service.EmailService;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.*;

public class SMTPHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(SMTPHandler.class.getName());

    private final Socket socket;
    private final EmailService emailService;

    public SMTPHandler(Socket socket, EmailService emailService) {
        this.socket = socket;
        this.emailService = emailService;
    }

    @Override
    public void run() {
        logger.info("Connection accepted from " + socket.getRemoteSocketAddress());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            out.write("220 Simple SMTP Server Ready\r\n");
            out.flush();
            logger.info("Sent: 220 Simple SMTP Server Ready");

            String from = null;
            String to = null;
            String line;

            while ((line = in.readLine()) != null) {
                logger.info("Received: " + line);

                if (line.startsWith("HELO") || line.startsWith("EHLO")) {
                    out.write("250 Hello\r\n");
                    out.flush();
                    logger.info("Sent: 250 Hello");

                } else if (line.startsWith("MAIL FROM:")) {
                    from = line.substring(10).trim();
                    out.write("250 OK\r\n");
                    out.flush();
                    logger.info("MAIL FROM detected: " + from);

                } else if (line.startsWith("RCPT TO:")) {
                    to = line.substring(8).trim().replace("<", "").replace(">", "");
                    out.write("250 OK\r\n");
                    out.flush();
                    logger.info("RCPT TO detected: " + to);

                    try {
                        validateRCPTEmail(to);
                    }catch(Exception e){
                        logger.log(Level.SEVERE, "Not a valid recipient email!", e);
                    }

                } else if (line.equals("DATA")) {
                    out.write("354 End data with <CR><LF>.<CR><LF>\r\n");
                    out.flush();
                    logger.info("Sent: 354 End data");

                    StringBuilder data = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        if (line.equals(".")) break;
                        data.append(line).append("\r\n");
                    }

                    saveEmail(to, from, data);
                    out.write("250 Message accepted\r\n");
                    out.flush();
                    logger.info("Email saved: From=" + from + ", To=" + to);

                } else if (line.equals("QUIT")) {
                    out.write("221 Bye\r\n");
                    out.flush();
                    logger.info("Client sent QUIT, closing connection");
                    break;

                } else {
                    out.write("500 Unrecognized command\r\n");
                    out.flush();
                    logger.warning("Unrecognized command: " + line);
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception handling client", ex);
        } finally {
            try {
                socket.close();
                logger.info("Connection closed: " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing socket", e);
            }
        }
    }

    // TODO
    private void validateRCPTEmail(String rcptEmail) throws Exception{
        return;
    }

    private void saveEmail(String to, String from, StringBuilder data) {
        String safeTo = to.replaceAll("[^a-zA-Z0-9@._-]", "_");
        String safeFrom = from.replaceAll("[^a-zA-Z0-9@._-]", "_");
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        String message = data.toString();
        byte[] rawData = message.getBytes(StandardCharsets.UTF_8);

        // Extract Subject
        String subject = "(No Subject)";
        String[] lines = message.split("\r\n");
        for (String line : lines) {
            if (line.isEmpty()) break; // end of headers
            if (line.toLowerCase().startsWith("subject:")) {
                subject = line.substring(8).trim(); // remove "Subject:" prefix
                break;
            }
        }
        logger.info("Subject extracted from email body data!");
        EmailData emailData = new EmailData(safeTo, safeFrom, subject, message, rawData, timestamp);

        emailService.saveEmail(emailData);
    }

    @Deprecated
    private void saveEmail(String to, String from, String body) throws IOException {
        String safeTo = to.replaceAll("[^a-zA-Z0-9@._-]", "_");
        String safeFrom = from.replaceAll("[^a-zA-Z0-9@._-]", "_");
        String timestamp = LocalDateTime.now().toString().replace(":", "-");

        File dir = new File("/tmp/emails/" + safeFrom);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create directory: " + dir);
        }

        File outFile = new File(dir, safeTo + "-" + timestamp + ".txt");
        try (FileWriter writer = new FileWriter(outFile, true)) {
            writer.write("From: " + from + "\n");
            writer.write("To: " + to + "\n");
            writer.write("Body:\n" + body + "\n");
            writer.write("----\n");
        }

        logger.info("Email written to disk: " + outFile.getAbsolutePath());
    }

}
