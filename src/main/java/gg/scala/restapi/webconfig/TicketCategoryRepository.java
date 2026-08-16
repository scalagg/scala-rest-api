package gg.scala.restapi.webconfig;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Luna
 */
public interface TicketCategoryRepository extends MongoRepository<TicketCategory, String> {
}
