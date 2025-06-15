package gg.scala.restapi.profile.stats.practice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PracticeProfileService {
    @Autowired
    private PracticeProfileRepository repository;

    public PracticeProfile getProfileById(String id) {
        return repository.findById(id).orElse(null);
    }

    public PracticeProfile getProfileByIdentifier(String identifier) {
        return repository.findByIdentifier(identifier);
    }
}