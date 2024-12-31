package gg.scala.restapi.profile.punishment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Punishment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Punishment {
    @Id
    private String uuid;

    private long addedAt;
    private String addedBy;
    private String addedOn;
    private String addedReason;
    private String category;
    private long duration;
    private String expireDate;
    private long removedAt;
    private String removedBy;
    private String removedOn;
    private String removedReason;
    private String target;
    private String targetCurrentIp;
}
