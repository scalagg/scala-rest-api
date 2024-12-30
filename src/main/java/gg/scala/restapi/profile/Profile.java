package gg.scala.restapi.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "LemonPlayer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    private String uniqueId;

    private List<String> ignoring = new ArrayList<>();
    private String ipAddress;
    private org.bson.Document metadata;
    private String name;
    private List<String> pastIpAddresses = new ArrayList<>();
    private List<String> specificPermissions = new ArrayList<>();
    private long timestamp;
}
