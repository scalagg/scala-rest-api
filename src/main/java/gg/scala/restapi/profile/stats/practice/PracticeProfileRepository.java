package gg.scala.restapi.profile.stats.practice;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeProfileRepository extends MongoRepository<PracticeProfile, String> {
    PracticeProfile findByIdentifier(String identifier);
}