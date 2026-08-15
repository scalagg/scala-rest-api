package gg.scala.restapi.messages;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Luna
 * @date 2026-08-15
 */
public interface DirectMessageReplyRepository extends MongoRepository<DirectMessageReply, String> {
    List<DirectMessageReply> findByMessageIdOrderByCreatedAtAsc(String messageId);
}
