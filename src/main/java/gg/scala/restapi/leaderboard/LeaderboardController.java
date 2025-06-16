package gg.scala.restapi.leaderboard;

import gg.scala.restapi.util.AppLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {
    private final AppLogger logger = AppLogger.getLogger(LeaderboardController.class);
    
    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public LeaderboardResponse getDefaultLeaderboard() {
        logger.info("Default leaderboard requested");
        return leaderboardService.getDefaultLeaderboard();
    }

    @GetMapping("/{statType}")
    public LeaderboardResponse getLeaderboard(@PathVariable String statType) {
        logger.info("Leaderboard requested for stat type: %s", statType);
        return leaderboardService.getLeaderboard(statType);
    }
} 