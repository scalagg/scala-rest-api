package gg.scala.restapi.forums.thread;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import gg.scala.restapi.forums.forums.Forum;
import gg.scala.restapi.forums.forums.ForumService;
import gg.scala.restapi.util.ForumThreadResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forum/thread")
public class ForumThreadController {
    private final ForumThreadService forumThreadService;
    private final ForumThreadResponseUtil forumThreadResponseUtil;
    private final ForumService forumService;

    public ForumThreadController(ForumThreadService forumThreadService, ForumThreadResponseUtil forumThreadResponseUtil, ForumService forumService) {
        this.forumThreadService = forumThreadService;
        this.forumThreadResponseUtil = forumThreadResponseUtil;
        this.forumService = forumService;
    }

    @PostMapping
    public ResponseEntity<ForumThread> createThread(@RequestBody ForumThread forumThread) {
        if (forumThreadService.getThread(forumThread.getId()) != null) {
            return ResponseEntity.badRequest().build();
        }

        Forum forum = forumService.getForumByNameOrId(forumThread.getForum());
        if (forum == null) {
            return ResponseEntity.badRequest().build();
        }

        forum.setThreadAmount(forum.getThreadAmount() + 1);
        forumService.save(forum);

        return ResponseEntity.ok(forumThreadService.save(forumThread));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumThread> getThread(@PathVariable String id) {
        ForumThread thread = forumThreadService.getThread(id);
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(forumThreadResponseUtil.transform(thread));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumThread> updateThread(@PathVariable String id, @RequestBody JsonNode body) {
        ForumThread thread = forumThreadService.getThread(id);
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }

        if (body.has("title")) {
            thread.setTitle(body.get("title").asText());
        }

        if (body.has("body")) {
            thread.setBody(body.get("body").asText());
        }

        if (body.has("lastEditedBy")) {
            thread.setLastEditedBy(body.get("lastEditedBy").asText());
        }

        if (body.has("lastEditedAt")) {
            thread.setLastEditedAt(body.get("lastEditedAt").asLong());
        }

        if (body.has("lastReplyAt")) {
            thread.setLastReplyAt(body.get("lastReplyAt").asLong());
        }

        if (body.has("pinned")) {
            thread.setPinned(body.get("pinned").asBoolean());
        }

        if (body.has("locked")) {
            thread.setLocked(body.get("locked").asBoolean());
        }

        return ResponseEntity.ok(forumThreadService.save(thread));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(@PathVariable String id) {
        ForumThread thread = forumThreadService.getThread(id);
        if (thread == null) {
            return ResponseEntity.notFound().build();
        }

        Forum forum = forumService.getForumByNameOrId(thread.getForum());
        if (forum != null) {
            forum.setThreadAmount(forum.getThreadAmount() - 1);
            forumService.save(forum);
        }

        forumThreadService.deleteThread(id);
        return ResponseEntity.ok().build();
    }
}
