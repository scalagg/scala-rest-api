package gg.scala.restapi.profile.comments;

import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/forum/account/comment")
@Tag(name = "Comment Management", description = "APIs for managing profile comments and interactions")
public class CommentController {
    private final ProfileService profileService;
    private final CommentService commentService;
    private final NotificationRepository notificationRepository;

    public CommentController(ProfileService profileService, CommentService commentService, NotificationRepository notificationRepository) {
        this.profileService = profileService;
        this.commentService = commentService;
        this.notificationRepository = notificationRepository;
    }

    @Operation(summary = "Create comment", description = "Creates a new comment on a user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment created successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PostMapping
    public ResponseEntity<Void> createComment(
            @Parameter(description = "Comment creation request") @RequestBody Comment comment) {
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

    @Operation(summary = "Delete comment", description = "Deletes a comment from a user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Comment or profile not found")
    })
    @DeleteMapping("/{profileId}/{uuid}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Profile UUID") @PathVariable String profileId,
            @Parameter(description = "Comment UUID") @PathVariable String uuid) {
        Comment comment = commentService.getComment(uuid);
        if (comment == null || !comment.getProfileId().equals(profileId)) {
            return ResponseEntity.notFound().build();
        }

        commentService.deleteComment(uuid);
        return ResponseEntity.ok().build();
    }
}
