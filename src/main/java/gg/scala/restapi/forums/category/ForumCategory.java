package gg.scala.restapi.forums.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ForumCategory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForumCategory {
    @Id
    private String id;

    private String name;
    private int weight;
}
