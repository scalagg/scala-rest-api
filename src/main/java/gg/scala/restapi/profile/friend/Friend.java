package gg.scala.restapi.profile.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Friendship")
@Data
@AllArgsConstructor
public class Friend {
    @Id
    private String identifier;

    private String player1;
    private String player2;
    private long startedOn;
}
