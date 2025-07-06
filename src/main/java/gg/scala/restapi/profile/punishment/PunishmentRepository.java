package gg.scala.restapi.profile.punishment;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PunishmentRepository extends MongoRepository<Punishment, String> {
    List<Punishment> findAllByTarget(String target);
    Optional<Punishment> findByUuid(String uuid);
}
