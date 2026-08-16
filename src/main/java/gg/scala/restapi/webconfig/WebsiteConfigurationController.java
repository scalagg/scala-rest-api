package gg.scala.restapi.webconfig;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @author Luna
 */
@RestController
@RequestMapping("/website/config")
public class WebsiteConfigurationController {
    private final ApplicationCategoryRepository applicationCategories;
    private final TicketCategoryRepository ticketCategories;
    private final ProfileBannerRepository profileBanners;

    public WebsiteConfigurationController(ApplicationCategoryRepository applicationCategories, TicketCategoryRepository ticketCategories, ProfileBannerRepository profileBanners) {
        this.applicationCategories = applicationCategories;
        this.ticketCategories = ticketCategories;
        this.profileBanners = profileBanners;
    }

    @GetMapping("/application-categories")
    public List<ApplicationCategory> applicationCategories() {
        return applicationCategories.findAll().stream().sorted(Comparator.comparingInt(ApplicationCategory::getOrder)).toList();
    }

    @PostMapping("/application-categories")
    public ApplicationCategory createApplicationCategory(@RequestBody ApplicationCategory category) {
        category.setId(UUID.randomUUID().toString());
        return applicationCategories.save(category);
    }

    @PutMapping("/application-categories/{id}")
    public ResponseEntity<ApplicationCategory> updateApplicationCategory(@PathVariable String id, @RequestBody ApplicationCategory category) {
        if (!applicationCategories.existsById(id)) return ResponseEntity.notFound().build();
        category.setId(id);
        return ResponseEntity.ok(applicationCategories.save(category));
    }

    @DeleteMapping("/application-categories/{id}")
    public ResponseEntity<Void> deleteApplicationCategory(@PathVariable String id) {
        if (!applicationCategories.existsById(id)) return ResponseEntity.notFound().build();
        applicationCategories.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ticket-categories")
    public List<TicketCategory> ticketCategories() {
        return ticketCategories.findAll().stream().sorted(Comparator.comparingInt(TicketCategory::getOrder)).toList();
    }

    @PostMapping("/ticket-categories")
    public TicketCategory createTicketCategory(@RequestBody TicketCategory category) {
        category.setId(UUID.randomUUID().toString());
        return ticketCategories.save(category);
    }

    @PutMapping("/ticket-categories/{id}")
    public ResponseEntity<TicketCategory> updateTicketCategory(@PathVariable String id, @RequestBody TicketCategory category) {
        if (!ticketCategories.existsById(id)) return ResponseEntity.notFound().build();
        category.setId(id);
        return ResponseEntity.ok(ticketCategories.save(category));
    }

    @DeleteMapping("/ticket-categories/{id}")
    public ResponseEntity<Void> deleteTicketCategory(@PathVariable String id) {
        if (!ticketCategories.existsById(id)) return ResponseEntity.notFound().build();
        ticketCategories.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile-banners")
    public List<ProfileBanner> profileBanners() {
        return profileBanners.findAll().stream().sorted(Comparator.comparingInt(ProfileBanner::getOrder)).toList();
    }

    @PostMapping("/profile-banners")
    public ProfileBanner createProfileBanner(@RequestBody ProfileBanner banner) {
        banner.setId(UUID.randomUUID().toString());
        return profileBanners.save(banner);
    }

    @PutMapping("/profile-banners/{id}")
    public ResponseEntity<ProfileBanner> updateProfileBanner(@PathVariable String id, @RequestBody ProfileBanner banner) {
        if (!profileBanners.existsById(id)) return ResponseEntity.notFound().build();
        banner.setId(id);
        return ResponseEntity.ok(profileBanners.save(banner));
    }

    @DeleteMapping("/profile-banners/{id}")
    public ResponseEntity<Void> deleteProfileBanner(@PathVariable String id) {
        if (!profileBanners.existsById(id)) return ResponseEntity.notFound().build();
        profileBanners.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
