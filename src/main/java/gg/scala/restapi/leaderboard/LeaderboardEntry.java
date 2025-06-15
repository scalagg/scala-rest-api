package gg.scala.restapi.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private String uuid;
    private String value;
    private String name;
    private String color;
} 