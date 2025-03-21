package ahmfps;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String toEmail, String subject, String body) {
        String fromEmail = "apuhostelmanagement@gmail.com";  
        String password = "jkqkaxlasqcekftv";

        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with the mail server
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password); // Use environment variable for password
            }
        });

        try {
            // Create a new email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));  // Set the sender's email address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Set recipient email
            message.setSubject(subject);  // Set the subject
            message.setText(body);  // Set the email body

            // Send the email
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error in sending email.");
        }
    }
}
