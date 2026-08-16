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
@Document(collection = "WebsiteProfileBanner")
public class ProfileBanner {
    @Id
    private String id;
    private String name;
    private int order = 5;
    private int level = 1;
    private String image;
    private List<String> allowedRanks = new ArrayList<>();
}
