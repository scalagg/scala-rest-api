package gg.scala.restapi.rank;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankRepository extends MongoRepository<Rank, String> {
    Optional<Rank> findByName(String name);
}
