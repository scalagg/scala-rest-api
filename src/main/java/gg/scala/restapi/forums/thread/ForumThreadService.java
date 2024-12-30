package gg.scala.restapi.forums.thread;

import org.springframework.stereotype.Service;

@Service
public class ForumThreadService {
    private final ForumThreadRepository forumThreadRepository;

    public ForumThreadService(ForumThreadRepository forumThreadRepository) {
        this.forumThreadRepository = forumThreadRepository;
    }

    public ForumThread createThread(ForumThread forumThread) {
        return forumThreadRepository.save(forumThread);
    }

    public ForumThread getThread(String id) {
        return forumThreadRepository.findById(id).orElse(null);
    }

    public void deleteThread(String id) {
        forumThreadRepository.deleteById(id);
    }
}
