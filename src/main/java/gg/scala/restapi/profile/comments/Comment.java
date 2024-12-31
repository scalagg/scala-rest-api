package gg.scala.restapi.profile.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ProfileComment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String uuid;

    private String author;
    private String content;
    private long addedAt;
    private String profileId;
}
