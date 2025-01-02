package gg.scala.restapi.tickets;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TicketReplyResponse extends TicketReply {
    private final String authorName;
    private final String authorColor;

    public TicketReplyResponse(TicketReply ticket, String authorName, String authorColor) {
        super(ticket.getId(), ticket.getAuthor(), ticket.getBody(), ticket.getCreatedAt(), ticket.getParentTicketId());
        this.authorName = authorName;
        this.authorColor = authorColor;
    }
}
