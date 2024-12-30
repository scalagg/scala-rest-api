package gg.scala.restapi.rank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Rank")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rank {
    @Id
    private String uuid;

    private List<String> children; // Check what this is actually a list of
    private String color;
    private String displayName;
    private boolean grantable;
    private String name;
    private List<String> permissions;
    private String prefix;
    private List<String> serverScopes;
    private String suffix;
    private boolean visible;
    private int weight;
}
