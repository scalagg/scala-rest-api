package gg.scala.restapi.webconfig;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luna
 */
@Data
@Document(collection = "WebsiteApplicationCategory")
public class ApplicationCategory {
    @Id
    private String id;
    private String name;
    private String slug;
    private int order = 5;
    private List<String> moderationRanks = new ArrayList<>();
    private List<String> questions = new ArrayList<>();
}
