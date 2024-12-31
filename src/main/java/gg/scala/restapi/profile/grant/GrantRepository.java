package gg.scala.restapi.profile.grant;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrantRepository extends MongoRepository<Grant, String> {
    List<Grant> findAllByTarget(String target);
    List<Grant> findAllByRankId(String rankId);
}
