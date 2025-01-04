package gg.scala.restapi.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerType {
    SOLARA(
            "Solara",
            "https://solara.gg"
    );

    private final String name;
    private final String url;
}
