package gg.scala.restapi.tickets;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import gg.scala.restapi.account.AccountService;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.server.ServerType;
import gg.scala.restapi.util.UUIDCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Luna
 */
@Service
public class EmailService {
    
    private final Resend resend;
    private final AccountService accountService;
    private final ProfileService profileService;

    @Value("${server.type}")
    private ServerType serverType;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("classpath:email-ticket-reply.html")
    private Resource emailTicketReply;

    @Value("classpath:email-ticket-status.html")
    private Resource emailTicketStatus;

    @Value("classpath:email-ticket-creation.html")
    private Resource emailTicketCreation;

    public EmailService(Resend resend, AccountService accountService, ProfileService profileService) {
        this.resend = resend;
        this.accountService = accountService;
        this.profileService = profileService;
    }

    public void sendTicketReplyNotification(String ticketId, String ticketTitle, String targetUserUuid, String replierUuid, String replyContent) {
        try {
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
            
            String htmlContent = new String(emailTicketReply.getInputStream().readAllBytes())
                    .replace("{{siteName}}", serverType.getName())
                    .replace("{{serverName}}", serverType.getName())
                    .replace("{{websiteUrl}}", serverType.getUrl())
                    .replace("{{ticketTitle}}", ticketTitle)
                    .replace("{{ticketId}}", ticketId)
                    .replace("{{replierName}}", replierName)
                    .replace("{{replyContent}}", replyContent)
                    .replace("{{ticketUrl}}", serverType.getUrl() + "/ticket/" + ticketId);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(email)
                    .subject("New Reply on Your Support Ticket - " + serverType.getName())
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("Ticket reply email sent to: " + email + " (ID: " + response.getId() + ")");
        } catch (IOException | ResendException e) {
            System.err.println("Failed to send ticket reply email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendTicketStatusNotification(String ticketId, String ticketTitle, String targetUserUuid, String newStatus, String updatedByUuid) {
        try {
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
                updatedByName = updatedByUuid;
            }
            
            String htmlContent = new String(emailTicketStatus.getInputStream().readAllBytes())
                    .replace("{{siteName}}", serverType.getName())
                    .replace("{{serverName}}", serverType.getName())
                    .replace("{{websiteUrl}}", serverType.getUrl())
                    .replace("{{ticketTitle}}", ticketTitle)
                    .replace("{{ticketId}}", ticketId)
                    .replace("{{newStatus}}", newStatus)
                    .replace("{{updatedByName}}", updatedByName)
                    .replace("{{ticketUrl}}", serverType.getUrl() + "/ticket/" + ticketId);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(email)
                    .subject("Support Ticket Status Update - " + serverType.getName())
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("Ticket status email sent to: " + email + " (ID: " + response.getId() + ")");
        } catch (IOException | ResendException e) {
            System.err.println("Failed to send ticket status email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendTicketCreationNotification(String ticketId, String ticketTitle, String targetUserUuid) {
        try {
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

            String htmlContent = new String(emailTicketCreation.getInputStream().readAllBytes())
                    .replace("{{siteName}}", serverType.getName())
                    .replace("{{serverName}}", serverType.getName())
                    .replace("{{websiteUrl}}", serverType.getUrl())
                    .replace("{{ticketTitle}}", ticketTitle)
                    .replace("{{ticketId}}", ticketId)
                    .replace("{{ticketUrl}}", serverType.getUrl() + "/ticket/" + ticketId);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(email)
                    .subject("Support Ticket Created - " + serverType.getName())
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("Ticket creation email sent to: " + email + " (ID: " + response.getId() + ")");
        } catch (IOException | ResendException e) {
            System.err.println("Failed to send ticket creation email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
