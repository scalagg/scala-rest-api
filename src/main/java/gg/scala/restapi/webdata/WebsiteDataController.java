package gg.scala.restapi.webdata;

import gg.scala.restapi.util.AppLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/websiteData")
public class WebsiteDataController {
    private final AppLogger logger = AppLogger.getLogger(WebsiteDataController.class);
    private final WebsiteDataService service;

    @Autowired
    public WebsiteDataController(WebsiteDataService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WebsiteDataEntry>> getAllEntries() {
        logger.info("All website data entries requested");
        List<WebsiteDataEntry> entries = service.getAllEntries();
        logger.debug("Retrieved %d website data entries", entries.size());
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebsiteDataEntry> getEntry(@PathVariable String id) {
        logger.info("Website data entry requested for ID: %s", id);
        
        WebsiteDataEntry entry = service.getEntry(id);
        if (entry == null) {
            logger.debug("Website data entry not found for ID: %s, returning default", id);
            if ("headline".equals(id)) {
                return ResponseEntity.ok(new WebsiteDataEntry(id, "Welcome to Scala Default Headline", "Welcome to Scala Default Headline", "#FFFFFF"));
            }
            if ("headlineColor".equals(id)) {
                return ResponseEntity.ok(new WebsiteDataEntry(id, "#FFFFFF", "", "#FFFFFF"));
            }
            return ResponseEntity.ok(new WebsiteDataEntry(id, "", "", ""));
        }
        logger.debug("Website data entry found for ID: %s", id);
        return ResponseEntity.ok(entry);
    }

    @PostMapping
    public ResponseEntity<Void> addEntry(@RequestBody WebsiteDataEntry entry) {
        logger.info("Website data entry creation requested for ID: %s", entry.getId());
        service.saveEntry(entry);
        logger.success("Website data entry created successfully for ID: %s", entry.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editEntry(@PathVariable String id, @RequestBody WebsiteDataEntry entry) {
        logger.info("Website data entry update requested for ID: %s", id);
        
        WebsiteDataEntry existing = service.getEntry(id);
        if (existing == null) {
            logger.warn("Website data entry not found for update ID: %s", id);
            return ResponseEntity.notFound().build();
        }
        existing.setValue(entry.getValue());
        existing.setHeadline(entry.getHeadline());
        existing.setHeadlineColor(entry.getHeadlineColor());
        service.saveEntry(existing);
        logger.success("Website data entry updated successfully for ID: %s", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable String id) {
        logger.info("Website data entry deletion requested for ID: %s", id);
        
        WebsiteDataEntry existing = service.getEntry(id);
        if (existing == null) {
            logger.warn("Website data entry not found for deletion ID: %s", id);
            return ResponseEntity.notFound().build();
        }
        service.deleteEntry(id);
        logger.success("Website data entry deleted successfully for ID: %s", id);
        return ResponseEntity.ok().build();
    }
} 