package gg.scala.restapi.tickets;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketSuggestion {
    private final String staff;
    private final String status;
}
