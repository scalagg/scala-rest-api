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
    PVPLEAGUE(
            "PvPLeague",
            "https://pvpleague.club"
    ),
    ALPHA(
            "Alpha",
            "https://alpha.rip"
    );


    private final String name;
    private final String url;
}
