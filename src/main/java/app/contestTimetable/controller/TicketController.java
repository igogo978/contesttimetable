package app.contestTimetable.controller;


import app.contestTimetable.model.Job;
import app.contestTimetable.model.Ticket;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class TicketController {

    @Autowired
    TicketService ticketservice;

    @Autowired
    TicketRepository tickerrepository;


    @GetMapping(value = "/ticket")
    public ArrayList<Ticket> getTickets() {

        tickerrepository.deleteAll();
//        Ticket ticket = new Ticket();

//        ticket.setLocation("064652");
//        ticket.setSchoolid("064510");
//        tickerrepository.save(ticket);
//        ticket.setSchoolid("123456");
//        ticket.setLocation("123456");
//        tickerrepository.save(ticket);

        return ticketservice.getAll();

    }

}
