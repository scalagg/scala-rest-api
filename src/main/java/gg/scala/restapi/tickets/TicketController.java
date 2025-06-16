package gg.scala.restapi.tickets;

import com.fasterxml.jackson.databind.JsonNode;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
import gg.scala.restapi.util.UUIDCache;
import gg.scala.restapi.util.AppLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/forum/ticket")
@Tag(name = "Ticket Management", description = "APIs for managing support tickets and applications")
public class TicketController {
    private final AppLogger logger = AppLogger.getLogger(TicketController.class);
    private final TicketService ticketService;
    private final TicketReplyRepository ticketReplyRepository;
    private final ProfileService profileService;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public TicketController(TicketService ticketService, TicketReplyRepository ticketReplyRepository, ProfileService profileService, NotificationRepository notificationRepository, EmailService emailService) {
        this.ticketService = ticketService;
        this.ticketReplyRepository = ticketReplyRepository;
        this.profileService = profileService;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @Operation(summary = "Get ticket", description = "Retrieves a specific ticket by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket found", content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(
            @Parameter(description = "Ticket ID") @PathVariable String id) {
        logger.info("Ticket requested for ID: %s", id);
        
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            logger.warn("Ticket not found for ID: %s", id);
            return ResponseEntity.notFound().build();
        }

        logger.debug("Ticket found for ID: %s", id);
        return ResponseEntity.ok(map(ticket));
    }

    @Operation(summary = "Get player tickets", description = "Retrieves all tickets for a specific player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully", content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @GetMapping("/player/{id}")
    public ResponseEntity<List<TicketResponse>> getTicketForPlayer(
            @Parameter(description = "Player UUID") @PathVariable String id) {
        return ResponseEntity.ok(ticketService.getTickets(id).stream().map(this::map).toList());
    }

    @Operation(summary = "Get all tickets", description = "Retrieves all tickets in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully", content = @Content(schema = @Schema(implementation = TicketResponse.class)))
    })
    @GetMapping("/s/all")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAll().stream().map(this::map).toList());
    }

    @Operation(summary = "Get all application tickets", description = "Retrieves all application tickets in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application tickets retrieved successfully", content = @Content(schema = @Schema(implementation = TicketResponse.class)))
    })
    @GetMapping("/s/all/applications")
    public ResponseEntity<List<TicketResponse>> getAllApplicationTickets() {
        return ResponseEntity.ok(ticketService.getAllApplicationTickets().stream().map(this::map).toList());
    }

    @Operation(summary = "Get player application tickets", description = "Retrieves all application tickets for a specific player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application tickets retrieved successfully", content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @GetMapping("/player/{id}/applications")
    public ResponseEntity<List<TicketResponse>> getApplicationTicketsForPlayer(
            @Parameter(description = "Player UUID") @PathVariable String id) {
        return ResponseEntity.ok(ticketService.getApplicationTickets(id).stream().map(this::map).toList());
    }

    @Operation(summary = "Create ticket", description = "Creates a new support ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket created successfully", content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or ticket already exists")
    })
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Parameter(description = "Ticket creation request") @RequestBody Ticket ticket) {
        logger.info("Ticket creation requested for ID: %s, author: %s", ticket.getId(), ticket.getAuthor());
        
        if (ticketService.getById(ticket.getId()) != null) {
            logger.warn("Ticket already exists for ID: %s", ticket.getId());
            return ResponseEntity.badRequest().build();
        }

        Ticket savedTicket = ticketService.save(ticket);
        logger.success("Ticket created successfully for ID: %s", ticket.getId());
        
        // Send email notification to the ticket creator confirming the ticket was created
        try {
            emailService.sendTicketCreationNotification(
                    savedTicket.getId(),
                    savedTicket.getTitle(),
                    savedTicket.getAuthor()
            );
            logger.info("Ticket creation email sent for ID: %s", ticket.getId());
        } catch (Exception e) {
            logger.error("Failed to send ticket creation email for ID %s: %s", ticket.getId(), e.getMessage());
        }

        return ResponseEntity.ok(map(savedTicket));
    }

    @Operation(summary = "Create ticket reply", description = "Adds a reply to an existing ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reply created successfully", content = @Content(schema = @Schema(implementation = TicketReply.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or ticket not found")
    })
    @PostMapping("/{id}/reply")
    public ResponseEntity<TicketReply> createReply(
            @Parameter(description = "Ticket ID") @PathVariable String id,
            @Parameter(description = "Reply creation request") @RequestBody TicketReply ticketReply) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return ResponseEntity.badRequest().build();
        }

        // Don't send notification to yourself
        if (!ticket.getAuthor().equals(ticketReply.getAuthor())) {
            // Create in-app notification
            Notification notification = new Notification(
                    UUID.randomUUID().toString(),
                    ticket.getAuthor(),
                    System.currentTimeMillis(),
                    "{userName} has replied to your ticket! Click here to visit your ticket!",
                    Map.of("{userName}", UUIDCache.getName(UUID.fromString(ticketReply.getAuthor()))),
                    false
            );
            notificationRepository.save(notification);

            // Send email notification
            emailService.sendTicketReplyNotification(
                    ticket.getId(),
                    ticket.getTitle(),
                    ticket.getAuthor(),
                    ticketReply.getAuthor(),
                    ticketReply.getBody()
            );
        }

        ticketReply.setParentTicketId(id);
        return ResponseEntity.ok(ticketReplyRepository.save(ticketReply));
    }

    @Operation(summary = "Update ticket", description = "Updates an existing ticket's status and other properties")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket updated successfully", content = @Content(schema = @Schema(implementation = Ticket.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(
            @Parameter(description = "Ticket ID") @PathVariable String id,
            @Parameter(description = "Ticket update request") @RequestBody JsonNode body) {
        Ticket existing = ticketService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (body.has("status")) {
            String newStatus = body.get("status").asText();
            
            // Create in-app notification
            Notification notification = new Notification(
                    UUID.randomUUID().toString(),
                    existing.getAuthor(),
                    System.currentTimeMillis(),
                    "Your ticket {ticketName} has been marked as {statusUpdate}! Click here to visit your ticket!",
                    Map.of(
                            "{ticketName}", existing.getTitle(),
                            "{statusUpdate}", newStatus
                    ),
                    false
            );
            notificationRepository.save(notification);

            // Send email notification (we'll assume the system/staff is updating, so use a generic staff UUID)
            // In a real implementation, you'd pass the updating user's UUID from the request
            emailService.sendTicketStatusNotification(
                    existing.getId(),
                    existing.getTitle(),
                    existing.getAuthor(),
                    newStatus,
                    existing.getAuthor() // Placeholder - should be the actual updating user
            );

            existing.setStatus(newStatus);
        }

        if (body.has("lastEditedAt")) {
            existing.setLastEditedAt(body.get("lastEditedAt").asLong());
        }

        if (body.has("resolveSuggestion")) {
            existing.setResolveSuggestion(
                    new TicketSuggestion(
                        body.get("resolveSuggestion").get("staff").asText(),
                        body.get("resolveSuggestion").get("status").asText()
                    )
            );
        }

        return ResponseEntity.ok(ticketService.save(existing));
    }

    private TicketResponse map(Ticket ticket) {
        return new TicketResponse(
                ticket,
                ticketReplyRepository.findAllByParentTicketId(ticket.getId()).stream().map(reply -> new TicketReplyResponse(
                        reply,
                        UUIDCache.getName(UUID.fromString(reply.getAuthor())),
                        profileService.getActiveRank(reply.getAuthor()).getColor()
                )).toList(),
                UUIDCache.getName(UUID.fromString(ticket.getAuthor())),
                profileService.getActiveRank(ticket.getAuthor()).getColor()
        );
    }
}
