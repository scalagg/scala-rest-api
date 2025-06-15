package gg.scala.restapi.leaderboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {
    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public LeaderboardResponse getDefaultLeaderboard() {
        return leaderboardService.getDefaultLeaderboard();
    }

    @GetMapping("/{statType}")
    public LeaderboardResponse getLeaderboard(@PathVariable String statType) {
        return leaderboardService.getLeaderboard(statType);
    }
} 