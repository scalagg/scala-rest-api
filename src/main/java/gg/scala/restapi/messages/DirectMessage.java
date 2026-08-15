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
@Document(collection = "DirectMessages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessage {
    @Id
    private String uuid;
    private String title;
    private String content;
    private String author;
    private String recipient;
    private long createdAt;
    private Long editedAt;
}
