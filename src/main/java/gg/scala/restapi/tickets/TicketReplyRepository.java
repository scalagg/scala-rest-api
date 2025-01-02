package gg.scala.restapi.tickets;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReplyRepository extends MongoRepository<TicketReply, String> {
    List<TicketReply> findAllByParentTicketId(String parentTicketId);
}
