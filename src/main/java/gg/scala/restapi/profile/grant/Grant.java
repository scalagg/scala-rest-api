package gg.scala.restapi.profile.grant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Document(collection = "Grant")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grant {
    @Id
    private String uuid;

    private long addedAt;
    private String addedBy;
    private String addedOn;
    private String addedReason;
    private long duration;
    private String expireDate;
    private String rankId;
    private long removedAt;
    private String removedBy;
    private String removedOn;
    private String removedReason;
    private List<String> scopes;
    private String target;
}
