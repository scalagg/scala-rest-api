package gg.scala.restapi.profile.friend;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    private final FriendRepository friendRepository;

    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public List<Friend> findAllForPlayer(String player) {
        return friendRepository.findAllByPlayer1OrPlayer2(player, player);
    }

    public Friend save(Friend friend) {
        return friendRepository.save(friend);
    }

    public Friend findById(String id) {
        return friendRepository.findById(id).orElse(null);
    }

    public void delete(Friend friend) {
        friendRepository.delete(friend);
    }
}
