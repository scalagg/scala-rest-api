package gg.scala.restapi.tickets;

import com.fasterxml.jackson.databind.JsonNode;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
import gg.scala.restapi.util.UUIDCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/forum/ticket")
public class TicketController {
    private final TicketService ticketService;
    private final TicketReplyRepository ticketReplyRepository;
    private final ProfileService profileService;
    private final NotificationRepository notificationRepository;

    public TicketController(TicketService ticketService, TicketReplyRepository ticketReplyRepository, ProfileService profileService, NotificationRepository notificationRepository) {
        this.ticketService = ticketService;
        this.ticketReplyRepository = ticketReplyRepository;
        this.profileService = profileService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable String id) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(map(ticket));
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<List<TicketResponse>> getTicketForPlayer(@PathVariable String id) {
        return ResponseEntity.ok(ticketService.getTickets(id).stream().map(this::map).toList());
    }

    @GetMapping("/s/all")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAll().stream().map(this::map).toList());
    }

    @GetMapping("/s/all/applications")
    public ResponseEntity<List<TicketResponse>> getAllApplicationTickets() {
        return ResponseEntity.ok(ticketService.getAllApplicationTickets().stream().map(this::map).toList());
    }

    @GetMapping("/player/{id}/applications")
    public ResponseEntity<List<TicketResponse>> getApplicationTicketsForPlayer(@PathVariable String id) {
        return ResponseEntity.ok(ticketService.getApplicationTickets(id).stream().map(this::map).toList());
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody Ticket ticket) {
        if (ticketService.getById(ticket.getId()) != null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(map(ticketService.save(ticket)));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<TicketReply> createReply(@PathVariable String id, @RequestBody TicketReply ticketReply) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return ResponseEntity.badRequest().build();
        }

        Notification notification = new Notification(
                UUID.randomUUID().toString(),
                ticket.getAuthor(),
                System.currentTimeMillis(),
                "{userName} has replied to your thread/ticket! Click here to visit your ticket/thread!",
                Map.of("{userName}", UUIDCache.getName(UUID.fromString(ticketReply.getAuthor()))),
                false
        );
        notificationRepository.save(notification);

        ticketReply.setParentTicketId(id);
        return ResponseEntity.ok(ticketReplyRepository.save(ticketReply));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable String id, @RequestBody JsonNode body) {
        Ticket existing = ticketService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (body.has("status")) {
            Notification notification = new Notification(
                    UUID.randomUUID().toString(),
                    existing.getAuthor(),
                    System.currentTimeMillis(),
                    "Your ticket {ticketName} has been marked as {statusUpdate}! Click here to visit your ticket!",
                    Map.of(
                            "{ticketName}", existing.getTitle(),
                            "{statusUpdate}", body.get("status").asText()
                    ),
                    false
            );
            notificationRepository.save(notification);

            existing.setStatus(body.get("status").asText());
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
