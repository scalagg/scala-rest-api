package gg.scala.restapi.tickets;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {
    List<Ticket> findAllByAuthor(String author);
    List<Ticket> findAllByCategoryContainingIgnoreCase(String category);
    List<Ticket> findAllByAuthorAndCategoryContainingIgnoreCase(String author, String application);
}
