package gg.scala.restapi.anticheat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnticheatRepository extends MongoRepository<AnticheatLog, String> {
    @Query("{ 'uuid' : ?0 }")
    List<AnticheatLog> findAllByUuid(String uuid);
}
