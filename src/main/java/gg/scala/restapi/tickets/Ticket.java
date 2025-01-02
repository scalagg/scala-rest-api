package gg.scala.restapi.tickets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@Document(collection = "ForumTicket")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    private String id;

    private String title;
    private String category;
    private String status;

    private String author;
    private long createdAt;
    private long lastEditedAt;

    private Map<String, String> questions;
    private @Nullable String punishmentId;
    private @Nullable TicketSuggestion resolveSuggestion;
}
