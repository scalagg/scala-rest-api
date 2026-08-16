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
@Document(collection = "WebsiteTicketCategory")
public class TicketCategory {
    @Id
    private String id;
    private String name;
    private int order = 5;
    private List<String> moderationRanks = new ArrayList<>();
    private List<String> subcategories = new ArrayList<>();
    private String sideboxTitle;
    private String sideboxContent;
}
