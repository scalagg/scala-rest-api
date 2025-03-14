package gg.scala.restapi.forums.thread;

import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
import gg.scala.restapi.rank.Rank;
import gg.scala.restapi.rank.RankService;
import gg.scala.restapi.util.UUIDCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/forum/replies")
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

    @GetMapping
    public ResponseEntity<Iterable<ThreadReplyResponse>> getReplies() {
        return ResponseEntity.ok(
                threadReplyRepository.findAllByParentReplyIdIsNull()
                        .stream()
                        .map(this::getThreadReplyResponse)
                        .toList()
        );
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


    @PostMapping("/{threadId}")
    public ResponseEntity<ThreadReply> createReply(@PathVariable String threadId, @RequestBody ThreadReply threadReply) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReply(@PathVariable String id) {
        Optional<ThreadReply> threadReply = threadReplyRepository.findById(id);
        if (threadReply.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        threadReplyRepository.delete(threadReply.get());
        return ResponseEntity.ok().build();
    }
}
