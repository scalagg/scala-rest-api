package gg.scala.restapi.webdata;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WebsiteDataRepository extends MongoRepository<WebsiteDataEntry, String> {
} 