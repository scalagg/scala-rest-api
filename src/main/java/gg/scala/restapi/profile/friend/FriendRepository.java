package gg.scala.restapi.profile.friend;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends MongoRepository<Friend, String> {
    List<Friend> findAllByPlayer1OrPlayer2(String player, String player1);
}
