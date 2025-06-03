package gg.scala.restapi.tickets;

import gg.scala.restapi.account.AccountService;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.server.ServerType;
import gg.scala.restapi.util.UUIDCache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class EmailService {
    
    private final JavaMailSender javaMailSender;
    private final AccountService accountService;
    private final ProfileService profileService;

    @Value("${server.type}")
    private ServerType serverType;

    @Value("classpath:email-ticket-reply.html")
    private Resource emailTicketReply;

    @Value("classpath:email-ticket-status.html")
    private Resource emailTicketStatus;

    @Value("classpath:email-ticket-creation.html")
    private Resource emailTicketCreation;

    public EmailService(JavaMailSender javaMailSender, AccountService accountService, ProfileService profileService) {
        this.javaMailSender = javaMailSender;
        this.accountService = accountService;
        this.profileService = profileService;
    }

    public void sendTicketReplyNotification(String ticketId, String ticketTitle, String targetUserUuid, String replierUuid, String replyContent) {
        try {
            // Get the target user's email
            var account = accountService.getById(targetUserUuid);
            if (account.isEmpty()) {
                System.out.println("No account found for user: " + targetUserUuid);
                return;
            }

            String email = account.get().getEmail();
            if (email == null || email.isBlank()) {
                System.out.println("No email address found for user: " + targetUserUuid);
                return;
            }

            String replierName = UUIDCache.getName(UUID.fromString(replierUuid));
            
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("no-reply@scala.gg");
            helper.setTo(email);
            helper.setSubject("New Reply on Your Support Ticket - " + serverType.getName());

            String htmlContent = new String(emailTicketReply.getInputStream().readAllBytes())
                    .replace("{{siteName}}", serverType.getName())
                    .replace("{{serverName}}", serverType.getName())
                    .replace("{{websiteUrl}}", serverType.getUrl())
                    .replace("{{ticketTitle}}", ticketTitle)
                    .replace("{{ticketId}}", ticketId)
                    .replace("{{replierName}}", replierName)
                    .replace("{{replyContent}}", replyContent)
                    .replace("{{ticketUrl}}", serverType.getUrl() + "/ticket/" + ticketId);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            
            System.out.println("Ticket reply email sent to: " + email);
        } catch (MessagingException | IOException e) {
            System.err.println("Failed to send ticket reply email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendTicketStatusNotification(String ticketId, String ticketTitle, String targetUserUuid, String newStatus, String updatedByUuid) {
        try {
            // Get the target user's email
            var account = accountService.getById(targetUserUuid);
            if (account.isEmpty()) {
                System.out.println("No account found for user: " + targetUserUuid);
                return;
            }

            String email = account.get().getEmail();
            if (email == null || email.isBlank()) {
                System.out.println("No email address found for user: " + targetUserUuid);
                return;
            }

            String updatedByName;
            try {
                updatedByName = UUIDCache.getName(UUID.fromString(updatedByUuid));
            } catch (IllegalArgumentException e) {
                // Handle cases where updatedByUuid is not a valid UUID (e.g., "System")
                updatedByName = updatedByUuid;
            }
            
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("no-reply@scala.gg");
            helper.setTo(email);
            helper.setSubject("Support Ticket Status Update - " + serverType.getName());

            String htmlContent = new String(emailTicketStatus.getInputStream().readAllBytes())
                    .replace("{{siteName}}", serverType.getName())
                    .replace("{{serverName}}", serverType.getName())
                    .replace("{{websiteUrl}}", serverType.getUrl())
                    .replace("{{ticketTitle}}", ticketTitle)
                    .replace("{{ticketId}}", ticketId)
                    .replace("{{newStatus}}", newStatus)
                    .replace("{{updatedByName}}", updatedByName)
                    .replace("{{ticketUrl}}", serverType.getUrl() + "/ticket/" + ticketId);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            
            System.out.println("Ticket status email sent to: " + email);
        } catch (MessagingException | IOException e) {
            System.err.println("Failed to send ticket status email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendTicketCreationNotification(String ticketId, String ticketTitle, String targetUserUuid) {
        try {
            // Get the target user's email
            var account = accountService.getById(targetUserUuid);
            if (account.isEmpty()) {
                System.out.println("No account found for user: " + targetUserUuid);
                return;
            }

            String email = account.get().getEmail();
            if (email == null || email.isBlank()) {
                System.out.println("No email address found for user: " + targetUserUuid);
                return;
            }

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("no-reply@scala.gg");
            helper.setTo(email);
            helper.setSubject("Support Ticket Created - " + serverType.getName());

            String htmlContent = new String(emailTicketCreation.getInputStream().readAllBytes())
                    .replace("{{siteName}}", serverType.getName())
                    .replace("{{serverName}}", serverType.getName())
                    .replace("{{websiteUrl}}", serverType.getUrl())
                    .replace("{{ticketTitle}}", ticketTitle)
                    .replace("{{ticketId}}", ticketId)
                    .replace("{{ticketUrl}}", serverType.getUrl() + "/ticket/" + ticketId);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            
            System.out.println("Ticket creation email sent to: " + email);
        } catch (MessagingException | IOException e) {
            System.err.println("Failed to send ticket creation email: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 