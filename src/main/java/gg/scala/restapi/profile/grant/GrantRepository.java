package gg.scala.restapi.profile.grant;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrantRepository extends MongoRepository<Grant, String> {
    List<Grant> findAllByTarget(String target);

    @Query("{ $and: [ { 'target': ?0 }, { $or: [ { 'removedAt': { $lte: 0 } }, { $expr: { $gt: [ { $add: ['$addedAt', '$duration'] }, ?1 ] } } ] } ] }")
    List<Grant> findAllActiveGrantsByTarget(String target, long currentTime);
}
