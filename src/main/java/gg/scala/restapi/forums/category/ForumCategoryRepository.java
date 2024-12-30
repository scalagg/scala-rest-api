package gg.scala.restapi.forums.category;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumCategoryRepository extends MongoRepository<ForumCategory, String> { }
