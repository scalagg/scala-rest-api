package gg.scala.restapi.forums.thread;

import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.rank.RankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/forum/replies")
public class ThreadReplyController {
    private final ThreadReplyRepository threadReplyRepository;
    private final ForumThreadService forumThreadService;
    private final RankService rankService;
    private final ProfileService profileService;

    public ThreadReplyController(ThreadReplyRepository threadReplyRepository, ForumThreadService forumThreadService, RankService rankService, ProfileService profileService) {
        this.threadReplyRepository = threadReplyRepository;
        this.forumThreadService = forumThreadService;
        this.rankService = rankService;
        this.profileService = profileService;
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
        return new ThreadReplyResponse(
                threadReply,
                profileService.getActiveRank(threadReply.getAuthor()).getColor(),
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
