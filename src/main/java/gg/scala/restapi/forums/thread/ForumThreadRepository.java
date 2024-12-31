package gg.scala.restapi.forums.thread;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumThreadRepository extends MongoRepository<ForumThread, String> {
    List<ForumThread> findAllByForumIgnoreCase(String forum, Pageable pageable);
    List<ForumThread> findAllByAuthor(String author);
}
