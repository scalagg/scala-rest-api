package gg.scala.restapi.webdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/websiteData")
public class WebsiteDataController {
    private final WebsiteDataService service;

    @Autowired
    public WebsiteDataController(WebsiteDataService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WebsiteDataEntry>> getAllEntries() {
        return ResponseEntity.ok(service.getAllEntries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebsiteDataEntry> getEntry(@PathVariable String id) {
        WebsiteDataEntry entry = service.getEntry(id);
        if (entry == null) {
            if ("headline".equals(id)) {
                return ResponseEntity.ok(new WebsiteDataEntry(id, "Welcome to Scala Default Headline", "Welcome to Scala Default Headline", "#FFFFFF"));
            }
            if ("headlineColor".equals(id)) {
                return ResponseEntity.ok(new WebsiteDataEntry(id, "#FFFFFF", "", "#FFFFFF"));
            }
            return ResponseEntity.ok(new WebsiteDataEntry(id, "", "", ""));
        }
        return ResponseEntity.ok(entry);
    }

    @PostMapping
    public ResponseEntity<Void> addEntry(@RequestBody WebsiteDataEntry entry) {
        service.saveEntry(entry);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editEntry(@PathVariable String id, @RequestBody WebsiteDataEntry entry) {
        WebsiteDataEntry existing = service.getEntry(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.setValue(entry.getValue());
        existing.setHeadline(entry.getHeadline());
        existing.setHeadlineColor(entry.getHeadlineColor());
        service.saveEntry(existing);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable String id) {
        WebsiteDataEntry existing = service.getEntry(id);
        if (existing == null) return ResponseEntity.notFound().build();
        service.deleteEntry(id);
        return ResponseEntity.ok().build();
    }
} 