package gg.scala.restapi.profile;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "CorePlayerProfile")
public class CorePlayerProfile {
    @Id
    private String id;
    private Object activeXpBooster;
    private List<Object> availableXpBoosters;
    private int coins;
    private Map<String, Object> cosmeticConfigs;
    private int customTagTokens;
    private List<Map<String, Object>> equippedCosmetics;
    private Object equippedTag;
    private String experience;
    private String identifier;
    private int level;
    private List<Object> ownedCosmetics;
    private String hasVotedOnNameMC;
} 