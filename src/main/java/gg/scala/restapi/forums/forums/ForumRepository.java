package gg.scala.restapi.forums.forums;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {
    List<Forum> findAllByCategory(String category);
    Forum findByNameIgnoreCaseOrIdIgnoreCase(String name, String id);
}
