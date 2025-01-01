package gg.scala.restapi.forums.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ForumThreadReply")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreadReply {

    @Id
    private String uuid;

    private String author;
    private String thread;
    private String comment;
    private long createdAt;
    private long lastEditedAt;
    private String parentReplyId;
}
