package gg.scala.restapi.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Luna
 * @date 2026-08-15
 */
@Document(collection = "DirectMessageReplies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessageReply {
    @Id
    private String uuid;
    private String messageId;
    private String author;
    private String content;
    private long createdAt;
    private Long editedAt;
}
