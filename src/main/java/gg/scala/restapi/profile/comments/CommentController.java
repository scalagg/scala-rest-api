package gg.scala.restapi.profile.comments;

import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
import gg.scala.restapi.util.UUIDCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/forum/account/comment")
public class CommentController {
    private final ProfileService profileService;
    private final CommentService commentService;
    private final NotificationRepository notificationRepository;

    public CommentController(ProfileService profileService, CommentService commentService, NotificationRepository notificationRepository) {
        this.profileService = profileService;
        this.commentService = commentService;
        this.notificationRepository = notificationRepository;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody Comment comment) {
        if (profileService.getById(comment.getProfileId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        comment.setUuid(UUID.randomUUID().toString());
        comment.setAddedAt(System.currentTimeMillis());
        commentService.save(comment);

        if (!comment.getProfileId().equals(comment.getAuthor())) {
            Notification notification = new Notification(
                    UUID.randomUUID().toString(),
                    comment.getProfileId(),
                    System.currentTimeMillis(),
                    "{userName} has made a comment on your profile!",
                    Map.of("{userName}", UUIDCache.getName(UUID.fromString(comment.getAuthor()))),
                    false
            );
            notificationRepository.save(notification);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{profileId}/{uuid}")
    public ResponseEntity<Void> deleteComment(@PathVariable String profileId, @PathVariable String uuid) {
        Comment comment = commentService.getComment(uuid);
        if (comment == null || !comment.getProfileId().equals(profileId)) {
            return ResponseEntity.notFound().build();
        }

        commentService.deleteComment(uuid);
        return ResponseEntity.ok().build();
    }
}
