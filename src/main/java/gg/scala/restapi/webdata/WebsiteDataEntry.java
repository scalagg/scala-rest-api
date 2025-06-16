package gg.scala.restapi.webdata;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "website_data")
public class WebsiteDataEntry {
    @Id
    private String id;
    private String value;
    private String headline;
    private String headlineColor;

    public WebsiteDataEntry() {}
    public WebsiteDataEntry(String id, String value, String headline, String headlineColor) {
        this.id = id;
        this.value = value;
        this.headline = headline;
        this.headlineColor = headlineColor;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getHeadlineColor() { return headlineColor; }
    public void setHeadlineColor(String headlineColor) { this.headlineColor = headlineColor; }
} 