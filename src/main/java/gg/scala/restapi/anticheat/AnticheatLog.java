package gg.scala.restapi.anticheat;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PlayerLogs")
@Data
@AllArgsConstructor
public class AnticheatLog {
    @Id
    private String uuid;
    private String name;
    private String checkdata;
}
