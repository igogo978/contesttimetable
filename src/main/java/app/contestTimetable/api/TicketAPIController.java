package app.contestTimetable.api;


import app.contestTimetable.model.Ticket;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TicketAPIController {

    @Autowired
    TicketRepository ticketRepository;

    @GetMapping(value = "/api/ticket")
    public List<Ticket> getTickets() {
        List<Ticket> tickets = new ArrayList<>();
        ticketRepository.findAll().forEach(ticket -> tickets.add(ticket));

        return tickets;

    }




}
