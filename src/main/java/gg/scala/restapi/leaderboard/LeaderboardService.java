package gg.scala.restapi.leaderboard;

import gg.scala.restapi.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class LeaderboardService {
    private static final String BASE_KEY = "scalapractice:leaderboards:";
    private static final String END_KEY = ":global:final";
    private static final String[] STAT_TYPES = new String[] { "ELO" };

    @Autowired
    private RedisService redisService;

    public LeaderboardResponse getLeaderboard(String statType) {
        Map<String, List<LeaderboardEntry>> entries = new HashMap<>();
        redisService.executeCommand(jedis -> {
            List<LeaderboardEntry> entryModels = new ArrayList<>();
            String key = BASE_KEY + statType + END_KEY;
            var values = jedis.zrevrangeWithScores(key, 0, 9);
            for (var entry : values) {
                String uuid = entry.getElement();
                String value = String.valueOf(entry.getScore());
                // Name/color lookup can be improved if you have a cache/service
                String name = uuid;
                String color = "#FFD700"; // Gold as default
                entryModels.add(new LeaderboardEntry(uuid, value, name, color));
            }
            entries.put(statType, entryModels);
            return null;
        });
        return new LeaderboardResponse(statType, entries);
    }

    public LeaderboardResponse getDefaultLeaderboard() {
        return getLeaderboard("ELO");
    }
} 