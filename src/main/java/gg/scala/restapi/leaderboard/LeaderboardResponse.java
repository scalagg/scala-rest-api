package gg.scala.restapi.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class LeaderboardResponse {
    private String name;
    private Map<String, List<LeaderboardEntry>> entries;
} 