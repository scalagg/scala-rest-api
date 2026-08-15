package gg.scala.restapi.forums.thread;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadReplyRepository extends MongoRepository<ThreadReply, String> {
    List<ThreadReply> findAllByThread(String thread);
    List<ThreadReply> findAllByThreadAndParentReplyIdIsNull(String thread);
    List<ThreadReply> findAllByParentReplyId(String parentReplyId);
    List<ThreadReply> findAllByParentReplyIdIsNull();
}
