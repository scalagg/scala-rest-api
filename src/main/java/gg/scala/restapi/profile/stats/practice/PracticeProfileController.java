package gg.scala.restapi.profile.stats.practice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/practiceprofile")
public class PracticeProfileController {
    @Autowired
    private PracticeProfileService service;

    @GetMapping("/{id}")
    public PracticeProfile getProfile(@PathVariable String id) {
        PracticeProfile profile = service.getProfileById(id);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        return profile;
    }

    @GetMapping("/identifier/{identifier}")
    public PracticeProfile getProfileByIdentifier(@PathVariable String identifier) {
        PracticeProfile profile = service.getProfileByIdentifier(identifier);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        return profile;
    }
}