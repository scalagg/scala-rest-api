package gg.scala.restapi.profile.friend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum/friends")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<List<Friend>> getFriends(@PathVariable String uuid) {
        return ResponseEntity.ok(friendService.findAllForPlayer(uuid));
    }

    @PostMapping
    public ResponseEntity<Friend> addFriend(@RequestBody Friend friend) {
        if (friend.getIdentifier() == null || friend.getPlayer1() == null || friend.getPlayer2() == null
                || friend.getPlayer1().equals(friend.getPlayer2())) {
            return ResponseEntity.badRequest().build();
        }
        boolean exists = friendService.findAllForPlayer(friend.getPlayer1()).stream().anyMatch(existing ->
                (existing.getPlayer1().equals(friend.getPlayer1()) && existing.getPlayer2().equals(friend.getPlayer2()))
                        || (existing.getPlayer1().equals(friend.getPlayer2()) && existing.getPlayer2().equals(friend.getPlayer1())));
        if (exists) return ResponseEntity.badRequest().build();
        if (friend.getStartedOn() <= 0) friend.setStartedOn(System.currentTimeMillis());
        return ResponseEntity.ok(friendService.save(friend));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFriend(@PathVariable String id) {
        Friend friend = friendService.findById(id);
        if (friend == null) return ResponseEntity.notFound().build();
        friendService.delete(friend);
        return ResponseEntity.ok().build();
    }
}
