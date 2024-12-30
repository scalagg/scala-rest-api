package gg.scala.restapi.forums.thread;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumThreadRepository extends MongoRepository<ForumThread, String> { }
