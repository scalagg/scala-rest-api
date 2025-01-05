package gg.scala.restapi.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Document(collection = "KFriendsPlayer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KFriendsPlayer {
    @Id
    private String id;

    @Field(value = "uniqueId", targetType = FieldType.STRING)
    private String uniqueId;

    private List<String> bestFriends;
    private org.bson.Document socialMedia;
}
