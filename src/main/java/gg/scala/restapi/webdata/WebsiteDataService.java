package gg.scala.restapi.webdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WebsiteDataService {
    private final WebsiteDataRepository repository;

    @Autowired
    public WebsiteDataService(WebsiteDataRepository repository) {
        this.repository = repository;
    }

    public WebsiteDataEntry getEntry(String id) {
        Optional<WebsiteDataEntry> entry = repository.findById(id);
        return entry.orElse(null);
    }

    public List<WebsiteDataEntry> getAllEntries() {
        return repository.findAll();
    }

    public void saveEntry(WebsiteDataEntry entry) {
        repository.save(entry);
    }

    public void deleteEntry(String id) {
        repository.deleteById(id);
    }
} 