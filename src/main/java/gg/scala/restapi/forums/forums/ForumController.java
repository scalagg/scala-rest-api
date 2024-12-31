package gg.scala.restapi.forums.forums;

import gg.scala.restapi.forums.thread.ForumThread;
import gg.scala.restapi.forums.thread.ForumThreadService;
import gg.scala.restapi.profile.Profile;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.util.ForumThreadResponseUtil;
import gg.scala.restapi.util.UUIDCache;
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
        if (forumService.getForum(forum.getId()) != null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(forumService.save(forum));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Forum> getForum(@PathVariable String id, @RequestParam(defaultValue = "-1") int page) {
        Forum forum = forumService.getForumByNameOrId(id.replace("-", " "));
        if (forum == null) {
            return ResponseEntity.notFound().build();
        }

        List<ForumThreadResponse> threads = new ArrayList<>();
        if (page > 0) {
            Pageable pageable = Pageable.ofSize(10).withPage(page - 1);
            threads = forumThreadService.getThreads(forum.getId(), pageable)
                    .stream().map(responseUtil::transform)
                    .toList();
        }

        return ResponseEntity.ok(new ForumResponse(forum, threads));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Forum> updateForum(@PathVariable String id, @RequestBody Forum forum) {
        Forum forum1 = forumService.getForum(id);
        if (forum1 == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(forumService.updateForum(id, forum));
    }

}
