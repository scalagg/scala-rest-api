package gg.scala.restapi.messages;

import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Luna
 * @date 2026-08-15
 */
@RestController
@RequestMapping("/forum/messages")
public class DirectMessageController {
    private final DirectMessageRepository messageRepository;
    private final DirectMessageReplyRepository replyRepository;
    private final NotificationRepository notificationRepository;

    public DirectMessageController(DirectMessageRepository messageRepository, DirectMessageReplyRepository replyRepository, NotificationRepository notificationRepository) {
        this.messageRepository = messageRepository;
        this.replyRepository = replyRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{participant}")
    public List<DirectMessage> getMessages(@PathVariable String participant) {
        return messageRepository.findByAuthorOrRecipientOrderByCreatedAtDesc(participant, participant);
    }

    @GetMapping("/thread/{id}")
    public ResponseEntity<DirectMessageThread> getMessage(@PathVariable String id, @RequestParam String participant) {
        return messageRepository.findById(id)
                .filter(message -> isParticipant(message, participant))
                .map(message -> ResponseEntity.ok(new DirectMessageThread(message, replyRepository.findByMessageIdOrderByCreatedAtAsc(id))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DirectMessage> createMessage(@RequestBody CreateMessageRequest request) {
        if (invalid(request.title(), 64) || invalid(request.content(), 4096) || invalid(request.author(), 64) || invalid(request.recipient(), 64) || request.author().equals(request.recipient())) {
            return ResponseEntity.badRequest().build();
        }

        DirectMessage message = messageRepository.save(new DirectMessage(
                UUID.randomUUID().toString(), request.title().trim(), request.content().trim(),
                request.author(), request.recipient(), System.currentTimeMillis(), null));
        notify(message.getRecipient(), message.getAuthor(), "has sent you a direct message.", "/message/" + message.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/{id}/replies")
    public ResponseEntity<DirectMessageReply> createReply(@PathVariable String id, @RequestBody CreateReplyRequest request) {
        DirectMessage message = messageRepository.findById(id).orElse(null);
        if (message == null || !isParticipant(message, request.author()) || invalid(request.content(), 1024)) {
            return ResponseEntity.badRequest().build();
        }

        DirectMessageReply reply = replyRepository.save(new DirectMessageReply(
                UUID.randomUUID().toString(), id, request.author(), request.content().trim(), System.currentTimeMillis(), null));
        String target = request.author().equals(message.getAuthor()) ? message.getRecipient() : message.getAuthor();
        notify(target, request.author(), "has replied to the direct message.", "/message/" + message.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @DeleteMapping("/{messageId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable String messageId, @PathVariable String replyId, @RequestParam String author) {
        DirectMessageReply reply = replyRepository.findById(replyId).orElse(null);
        if (reply == null || !messageId.equals(reply.getMessageId())) return ResponseEntity.notFound().build();
        if (!author.equals(reply.getAuthor())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        replyRepository.delete(reply);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable String replyId, @RequestParam String author) {
        DirectMessageReply reply = replyRepository.findById(replyId).orElse(null);
        if (reply == null) return ResponseEntity.notFound().build();
        if (!author.equals(reply.getAuthor())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        replyRepository.delete(reply);
        return ResponseEntity.noContent().build();
    }

    private boolean isParticipant(DirectMessage message, String participant) {
        return participant.equals(message.getAuthor()) || participant.equals(message.getRecipient());
    }

    private boolean invalid(String value, int maximumLength) {
        return value == null || value.trim().length() < 3 || value.length() > maximumLength;
    }

    private void notify(String target, String actor, String text, String link) {
        notificationRepository.save(new Notification(
                UUID.randomUUID().toString(), target, System.currentTimeMillis(), text,
                Map.of("user", actor, "link", link), false));
    }

    public record CreateMessageRequest(String title, String content, String author, String recipient) {}
    public record CreateReplyRequest(String content, String author) {}
    public record DirectMessageThread(DirectMessage message, List<DirectMessageReply> replies) {}
}
