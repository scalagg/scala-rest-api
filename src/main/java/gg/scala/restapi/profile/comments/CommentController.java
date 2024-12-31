package gg.scala.restapi.profile.comments;

import gg.scala.restapi.profile.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/forum/account/comment")
public class CommentController {
    private final ProfileService profileService;
    private final CommentService commentService;

    public CommentController(ProfileService profileService, CommentService commentService) {
        this.profileService = profileService;
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody Comment comment) {
        if (profileService.getById(comment.getProfileId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        comment.setUuid(UUID.randomUUID().toString());
        comment.setAddedAt(System.currentTimeMillis());
        commentService.save(comment);

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
