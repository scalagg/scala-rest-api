package gg.scala.restapi.account;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Account")
@Data
@AllArgsConstructor
public class Account {

    @Id
    private final String uuid;

    private String email;
    @Nullable private String password;
    private String token;
    private org.bson.Document settings;
    private List<String> badges;
}
