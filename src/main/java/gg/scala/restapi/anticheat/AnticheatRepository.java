package gg.scala.restapi.anticheat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnticheatRepository extends MongoRepository<AnticheatLog, String> {
    List<AnticheatLog> findAllByUuid(String uuid);
}
