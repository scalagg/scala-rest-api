package gg.scala.restapi.forums.forums;

import gg.scala.restapi.forums.thread.ForumThread;
import gg.scala.restapi.forums.thread.ForumThreadService;
import gg.scala.restapi.profile.Profile;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.util.ForumThreadResponseUtil;
import gg.scala.restapi.util.UUIDCache;
import gg.scala.restapi.util.AppLogger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/forum/forum")
public class ForumController {
    private final AppLogger logger = AppLogger.getLogger(ForumController.class);
    private final ForumService forumService;
    private final ForumThreadService forumThreadService;
    private final ForumThreadResponseUtil responseUtil;

    public ForumController(ForumService forumService, ForumThreadService forumThreadService, ForumThreadResponseUtil responseUtil) {
        this.forumService = forumService;
        this.forumThreadService = forumThreadService;
        this.responseUtil = responseUtil;;
    }

    @PostMapping
    public ResponseEntity<Forum> createForum(@RequestBody Forum forum) {
        logger.info("Forum creation requested for ID: %s", forum.getId());
        
        if (forumService.getForum(forum.getId()) != null) {
            logger.warn("Forum already exists for ID: %s", forum.getId());
            return ResponseEntity.badRequest().build();
        }

        Forum savedForum = forumService.save(forum);
        logger.success("Forum created successfully for ID: %s", forum.getId());
        return ResponseEntity.ok(savedForum);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Forum> getForum(@PathVariable String id, @RequestParam(defaultValue = "-1") int page) {
        logger.info("Forum requested for ID: %s, page: %d", id, page);
        
        Forum forum = forumService.getForumByNameOrId(id.replace("-", " "));
        if (forum == null) {
            logger.warn("Forum not found for ID: %s", id);
            return ResponseEntity.notFound().build();
        }

        List<ForumThreadResponse> threads = new ArrayList<>();
        if (page > 0) {
            Pageable pageable = Pageable.ofSize(10).withPage(page - 1);
            threads = forumThreadService.getThreads(forum.getId(), pageable)
                    .stream().map(responseUtil::transform)
                    .toList();
        }

        logger.debug("Forum found for ID: %s with %d threads", id, threads.size());
        return ResponseEntity.ok(new ForumResponse(forum, threads));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Forum> updateForum(@PathVariable String id, @RequestBody Forum forum) {
        logger.info("Forum update requested for ID: %s", id);
        
        Forum forum1 = forumService.getForum(id);
        if (forum1 == null) {
            logger.warn("Forum not found for update ID: %s", id);
            return ResponseEntity.notFound().build();
        }

        forum.setCategory(forum1.getCategory());

        Forum updatedForum = forumService.updateForum(id, forum);
        logger.success("Forum updated successfully for ID: %s", id);
        return ResponseEntity.ok(updatedForum);
    }

}
