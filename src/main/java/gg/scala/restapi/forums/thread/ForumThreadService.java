package gg.scala.restapi.forums.thread;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumThreadService {
    private final ForumThreadRepository forumThreadRepository;

    public ForumThreadService(ForumThreadRepository forumThreadRepository) {
        this.forumThreadRepository = forumThreadRepository;
    }

    public ForumThread save(ForumThread forumThread) {
        return forumThreadRepository.save(forumThread);
    }

    public ForumThread getThread(String id) {
        return forumThreadRepository.findById(id).orElse(null);
    }

    public void deleteThread(String id) {
        forumThreadRepository.deleteById(id);
    }

    public List<ForumThread> getThreads(String forumId, Pageable pageable) {
        return forumThreadRepository.findAllByForumIgnoreCase(forumId, pageable);
    }

    public List<ForumThread> getThreadsByAuthor(String author) {
        return forumThreadRepository.findAllByAuthor(author);
    }
}
