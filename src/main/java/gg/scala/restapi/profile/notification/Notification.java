package gg.scala.restapi.profile.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "ProfileNotifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    private String uuid;

    private String target;
    private long createdAt;
    private String text;

    private Map<String, String> placeholders;
    private boolean read;
}
