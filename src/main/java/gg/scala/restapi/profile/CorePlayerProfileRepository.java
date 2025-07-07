package gg.scala.restapi.profile;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorePlayerProfileRepository extends MongoRepository<CorePlayerProfile, String> {
} 