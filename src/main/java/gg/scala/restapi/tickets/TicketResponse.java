package gg.scala.restapi.tickets;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TicketResponse extends Ticket {
    private final List<TicketReplyResponse> replies;
    private final String authorName;
    private final String authorColor;

    public TicketResponse(Ticket ticket, List<TicketReplyResponse> replies, String authorName, String authorColor) {
        super(ticket.getId(), ticket.getTitle(), ticket.getCategory(), ticket.getStatus(), ticket.getAuthor(), ticket.getCreatedAt(), ticket.getLastEditedAt(), ticket.getQuestions(), ticket.getPunishmentId(), ticket.getResolveSuggestion());
        this.replies = replies;
        this.authorName = authorName;
        this.authorColor = authorColor;
    }
}
