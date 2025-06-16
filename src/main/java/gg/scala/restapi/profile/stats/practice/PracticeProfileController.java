package gg.scala.restapi.profile.stats.practice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import gg.scala.restapi.util.AppLogger;

@RestController
@RequestMapping("/practiceprofile")
public class PracticeProfileController {
    private static final AppLogger log = AppLogger.getLogger(PracticeProfileController.class);
    @Autowired
    private PracticeProfileService service;

    @GetMapping("/{id}")
    public PracticeProfile getProfile(@PathVariable String id) {
        PracticeProfile profile = service.getProfileById(id);
        if (profile == null) {
            log.warn("Profile not found for id: %s", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        log.success("Returning found profile for id: %s", id);
        return profile;
    }
    @GetMapping("/test")
    public String test() {
        log.info("Test endpoint called");
        return "OK";
    }
    @GetMapping("/identifier/{identifier}")
    public PracticeProfile getProfileByIdentifier(@PathVariable String identifier) {
        log.info("getProfileByIdentifier called with: %s", identifier);
        PracticeProfile profile = service.getProfileByIdentifier(identifier);
        if (profile == null) {
            log.warn("Profile not found for identifier: %s - returning default profile", identifier);
            PracticeProfile defaultProfile = new PracticeProfile();
            defaultProfile.setIdentifier(identifier);
            defaultProfile.setGlobalStatistics(new HashMap<>());
            defaultProfile.setRankedStatistics(new HashMap<>());
            log.success("Returning default profile for identifier: %s", identifier);
            return defaultProfile;
        }
        log.success("Returning found profile for identifier: %s", identifier);
        return profile;
    }
}