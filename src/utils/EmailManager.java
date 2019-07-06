package src.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;


@SuppressWarnings("ALL")
public class EmailManager {

    //verified email address of the sender - DO NOT CHANGE
    private static final String FROM = "csc207atmmachine@gmail.com";
    private static final String FROMNAME = "CSC207 ATM Machine";

    //recipient of the email
    static String TO = "";

    // Amazon SES SMTP access credentials - DO NOT CHANGE
    private static final String SMTP_USERNAME = "AKIAJEXYXLYPMEVRBKHA";
    private static final String SMTP_PASSWORD = "BA7tSqa09XbDhXzHCQHM+5ENPwznLYWHpsiK+vMN9LJo";
    private static final String HOST = "email-smtp.us-east-1.amazonaws.com";
    private static final int PORT = 587;

    /**
     * sends an email from the ATM to *one* email address
     * @param recipientAddress email address of the recipient
     * @param emailSubject the text to be put in the email subject header
     * @param emailContent the body of the email
     * @return whether or not the email was sent successfully
     */
    public static boolean sendEmail(String recipientAddress, String emailSubject, String emailContent) {
        // Send the message.
        try
        {

            // Create a Properties object to contain connection configuration information.
            Properties props = System.getProperties();

            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            // Create a Session object to represent a mail session with the specified properties.
            Session session = Session.getDefaultInstance(props);

            // Create a message with the specified information.
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM,FROMNAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            msg.setSubject(emailSubject);
            msg.setContent(emailContent,"text/html");

            // Create a transport.
            Transport transport = session.getTransport();


            System.out.println("Sending...");
            // Connect to Amazon SES using the SMTP username and password specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
            transport.close();
            return true;
        }
        catch (Exception ex) {
            //error handling
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            return false;
        }
    }


    /**
     * Not used anywhere yet.
     * sends an email from the ATM to a list of email addresses
     * @param recipientList list of recipient email address of the recipient
     * @param emailSubject the text to be put in the email subject header
     * @param emailContent the body of the email
     * @return whether or not the email was sent successfully
     */
    public static boolean sendEmailMultiple(List<String> recipientList, String emailSubject, String emailContent) {

        try
        {
            // Create a Properties object to contain connection configuration information.
            Properties props = System.getProperties();

            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            // Create a Session object to represent a mail session with the specified properties.
            Session session = Session.getDefaultInstance(props);

            // Create a message with the specified information.
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM,FROMNAME));

            //add all emails in list as recipients
            for(String recipient: recipientList) {
                msg.addRecipient(Message.RecipientType.CC, new InternetAddress(recipient));
            }

            msg.setSubject(emailSubject);
            msg.setContent(emailContent,"text/html");

            // Create a transport.
            Transport transport = session.getTransport();

            // Send the message.
            System.out.println("Sending...");
            // Connect to Amazon SES using the SMTP username and password specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
            transport.close();
            return true;
        }
        catch (Exception ex) {
            //error handling
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            return false;
        }
    }
}