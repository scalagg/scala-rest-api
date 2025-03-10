package gg.scala.restapi.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerType {
    SCALA(
            "Scala",
            "https://scala.gg"
    ),
    POTPVP(
            "PotPvP",
            "https://potpvp.com"
    );

    private final String name;
    private final String url;
}
