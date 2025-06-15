package gg.scala.restapi.profile.stats.practice;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "PracticeProfile")
public class PracticeProfile {
    @Id
    private String id;

    private String botDivision;
    private Map<String, Object> botStatistics;
    private Map<String, Object> customLoadouts;
    private String[] favoriteKits;
    private Map<String, Object> globalStatistics;
    private String identifier;
    private Object rankedBan;
    private Map<String, Object> rankedStatistics;
    private Map<String, Object> unrankedStatistics;
}