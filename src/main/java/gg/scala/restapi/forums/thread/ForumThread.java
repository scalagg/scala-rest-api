package gg.scala.restapi.forums.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "ForumThread")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForumThread {
    @Id
    private String id;

    private String title;
    private String body;
    private String forum;
    private String author;
    private long createdAt;
    private String lastEditedBy;
    private long lastEditedAt;
    private long lastReplyAt;
    private boolean pinned;
    private boolean locked;
    private String parentThreadId;
    private String imageUrl;
}
