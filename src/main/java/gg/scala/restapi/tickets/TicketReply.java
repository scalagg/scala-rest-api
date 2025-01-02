package gg.scala.restapi.tickets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ForumTicketReply")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReply {

    @Id
    private String id;

    private String author;
    private String body;
    private long createdAt;
    private String parentTicketId;
}
