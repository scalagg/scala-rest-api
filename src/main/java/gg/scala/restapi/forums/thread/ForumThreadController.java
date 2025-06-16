package gg.scala.restapi.forums.thread;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import gg.scala.restapi.forums.forums.Forum;
import gg.scala.restapi.forums.forums.ForumService;
import gg.scala.restapi.util.ForumThreadResponseUtil;
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

@RestController
@RequestMapping("/forum/thread")
@Tag(name = "Forum Thread Management", description = "APIs for managing forum threads and discussions")
public class ForumThreadController {
    private final AppLogger logger = AppLogger.getLogger(ForumThreadController.class);
    private final ForumThreadService forumThreadService;
    private final ForumThreadResponseUtil forumThreadResponseUtil;
    private final ForumService forumService;

    public ForumThreadController(ForumThreadService forumThreadService, ForumThreadResponseUtil forumThreadResponseUtil, ForumService forumService) {
        this.forumThreadService = forumThreadService;
        this.forumThreadResponseUtil = forumThreadResponseUtil;
        this.forumService = forumService;
    }

    @Operation(summary = "Create thread", description = "Creates a new forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thread created successfully", content = @Content(schema = @Schema(implementation = ForumThread.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or thread already exists")
    })
    @PostMapping
    public ResponseEntity<ForumThread> createThread(
            @Parameter(description = "Thread creation request") @RequestBody ForumThread forumThread) {
        logger.info("Thread creation requested for ID: %s, forum: %s", forumThread.getId(), forumThread.getForum());
        
        if (forumThreadService.getThread(forumThread.getId()) != null) {
            logger.warn("Thread already exists for ID: %s", forumThread.getId());
            return ResponseEntity.badRequest().build();
        }

        Forum forum = forumService.getForumByNameOrId(forumThread.getForum());
        if (forum == null) {
            logger.warn("Forum not found for thread creation: %s", forumThread.getForum());
            return ResponseEntity.badRequest().build();
        }

        forum.setThreadAmount(forum.getThreadAmount() + 1);
        forumService.save(forum);

        ForumThread savedThread = forumThreadService.save(forumThread);
        logger.success("Thread created successfully for ID: %s", forumThread.getId());
        return ResponseEntity.ok(savedThread);
    }

    @Operation(summary = "Get thread", description = "Retrieves a specific forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thread found", content = @Content(schema = @Schema(implementation = ForumThread.class))),
        @ApiResponse(responseCode = "404", description = "Thread not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ForumThread> getThread(
            @Parameter(description = "Thread ID") @PathVariable String id) {
        logger.info("Thread requested for ID: %s", id);
        
        ForumThread thread = forumThreadService.getThread(id);
        if (thread == null) {
            logger.warn("Thread not found for ID: %s", id);
            return ResponseEntity.notFound().build();
        }

        logger.debug("Thread found for ID: %s", id);
        return ResponseEntity.ok(forumThreadResponseUtil.transform(thread));
    }

    @Operation(summary = "Update thread", description = "Updates an existing forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thread updated successfully", content = @Content(schema = @Schema(implementation = ForumThread.class))),
        @ApiResponse(responseCode = "404", description = "Thread not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ForumThread> updateThread(
            @Parameter(description = "Thread ID") @PathVariable String id,
            @Parameter(description = "Thread update request") @RequestBody JsonNode body) {
        logger.info("Thread update requested for ID: %s", id);
        
        ForumThread thread = forumThreadService.getThread(id);
        if (thread == null) {
            logger.warn("Thread not found for update ID: %s", id);
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

        ForumThread updatedThread = forumThreadService.save(thread);
        logger.success("Thread updated successfully for ID: %s", id);
        return ResponseEntity.ok(updatedThread);
    }

    @Operation(summary = "Delete thread", description = "Deletes a forum thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thread deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Thread not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(
            @Parameter(description = "Thread ID") @PathVariable String id) {
        logger.info("Thread deletion requested for ID: %s", id);
        
        ForumThread thread = forumThreadService.getThread(id);
        if (thread == null) {
            logger.warn("Thread not found for deletion ID: %s", id);
            return ResponseEntity.notFound().build();
        }

        Forum forum = forumService.getForumByNameOrId(thread.getForum());
        if (forum != null) {
            forum.setThreadAmount(forum.getThreadAmount() - 1);
            forumService.save(forum);
        }

        forumThreadService.deleteThread(id);
        logger.success("Thread deleted successfully for ID: %s", id);
        return ResponseEntity.ok().build();
    }
}
