package gg.scala.restapi.forums.forums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Forum")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Forum {

    @Id
    private String id;
    private String name;
    private String description;
    private int weight;
    private boolean locked;
    private String category;
    private int threadAmount;
}
