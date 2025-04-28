package gg.scala.restapi.forums.thread;

import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
import gg.scala.restapi.rank.Rank;
import gg.scala.restapi.rank.RankService;
import gg.scala.restapi.util.UUIDCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/forum/replies")
@Tag(name = "Thread Reply Management", description = "APIs for managing forum thread replies and discussions")
public class ThreadReplyController {
    private final ThreadReplyRepository threadReplyRepository;
    private final ForumThreadService forumThreadService;
    private final RankService rankService;
    private final ProfileService profileService;
    private final NotificationRepository notificationRepository;

    public ThreadReplyController(ThreadReplyRepository threadReplyRepository, ForumThreadService forumThreadService, RankService rankService, ProfileService profileService, NotificationRepository notificationRepository) {
        this.threadReplyRepository = threadReplyRepository;
        this.forumThreadService = forumThreadService;
        this.rankService = rankService;
        this.profileService = profileService;
        this.notificationRepository = notificationRepository;
    }

    @Operation(summary = "Get all replies", description = "Retrieves all top-level replies in the forum")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Replies retrieved successfully", content = @Content(schema = @Schema(implementation = ThreadReplyResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Iterable<ThreadReplyResponse>> getReplies() {
        return ResponseEntity.ok(
                threadReplyRepository.findAllByParentReplyIdIsNull()
                        .stream()
                        .map(this::getThreadReplyResponse)
                        .toList()
        );
    }

    @Operation(summary = "Create reply", description = "Creates a new reply to a forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reply created successfully", content = @Content(schema = @Schema(implementation = ThreadReply.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or thread not found")
    })
    @PostMapping("/{threadId}")
    public ResponseEntity<ThreadReply> createReply(
            @Parameter(description = "Thread ID") @PathVariable String threadId,
            @Parameter(description = "Reply creation request") @RequestBody ThreadReply threadReply) {
        ForumThread thread = forumThreadService.getThread(threadId);
        if (thread == null) {
            return ResponseEntity.badRequest().build();
        }

        // Create notification
        if (!thread.getAuthor().equals(threadReply.getAuthor())) {
            Notification notification = new Notification(
                    UUID.randomUUID().toString(),
                    thread.getAuthor(),
                    System.currentTimeMillis(),
                    "{userName} has replied to your thread! Click here to visit your thread!",
                    Map.of("{userName}", UUIDCache.getName(UUID.fromString(threadReply.getAuthor()))),
                    false
            );
            notificationRepository.save(notification);
        }

        if (threadReply.getParentReplyId() != null) {
            Optional<ThreadReply> parentReply = threadReplyRepository.findById(threadReply.getParentReplyId());
            if (parentReply.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (!parentReply.get().getAuthor().equals(threadReply.getAuthor())) {
                Notification parentNotification = new Notification(
                        UUID.randomUUID().toString(),
                        parentReply.get().getAuthor(),
                        System.currentTimeMillis(),
                        "{userName} has replied to your reply! Click here to visit the thread!",
                        Map.of("{userName}", UUIDCache.getName(UUID.fromString(threadReply.getAuthor()))),
                        false
                );
                notificationRepository.save(parentNotification);
            }
        }

        threadReply.setThread(threadId);
        threadReply.setCreatedAt(System.currentTimeMillis());

        return ResponseEntity.ok(threadReplyRepository.save(threadReply));
    }

    @Operation(summary = "Delete reply", description = "Deletes a reply from a forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reply deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Reply not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReply(
            @Parameter(description = "Reply ID") @PathVariable String id) {
        Optional<ThreadReply> threadReply = threadReplyRepository.findById(id);
        if (threadReply.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        threadReplyRepository.delete(threadReply.get());
        return ResponseEntity.ok().build();
    }

    /**
     * Get all replies for a specific thread
     *
     * @param threadReply
     * @return ThreadReplyResponse
     */
    private ThreadReplyResponse getThreadReplyResponse(ThreadReply threadReply) {
        Rank activeRank = profileService.getActiveRank(threadReply.getAuthor());

        if (activeRank == null) {
            System.out.println("Found null rank for user with UUID: " + threadReply.getAuthor());
            return new ThreadReplyResponse(
                    threadReply,
                    "#FFFFFF",
                    threadReplyRepository.findAllByParentReplyId(threadReply.getUuid())
                            .stream()
                            .map(this::getThreadReplyResponse)
                            .toList()
            );
        }

        return new ThreadReplyResponse(
                threadReply,
                activeRank.getColor(),
                threadReplyRepository.findAllByParentReplyId(threadReply.getUuid())
                        .stream()
                        .map(this::getThreadReplyResponse)
                        .toList()
        );
    }
}
