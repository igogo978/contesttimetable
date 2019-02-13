package app.contestTimetable.service;


import app.contestTimetable.model.Ticket;
import app.contestTimetable.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketrepository;

    public ArrayList<Ticket> getAll() {

        ArrayList<Ticket> tickets = new ArrayList<>();

        ticketrepository.findAll().forEach(school -> tickets.add(school));

        return tickets;
    }
}
