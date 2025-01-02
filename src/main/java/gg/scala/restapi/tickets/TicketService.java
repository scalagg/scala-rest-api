package gg.scala.restapi.tickets;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public void delete(Ticket ticket) {
        ticketRepository.delete(ticket);
    }

    public Ticket getById(String id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public List<Ticket> getTickets(String author) {
        return ticketRepository.findAllByAuthor(author);
    }

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getAllApplicationTickets() {
        return ticketRepository.findAllByCategoryContainingIgnoreCase("application");
    }
}
