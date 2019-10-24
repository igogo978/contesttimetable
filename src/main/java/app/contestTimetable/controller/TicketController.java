package app.contestTimetable.controller;


import app.contestTimetable.model.Job;
import app.contestTimetable.model.Ticket;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Controller
public class TicketController {

    @Autowired
    TicketService ticketservice;

    @Autowired
    TicketRepository tickerrepository;


    @GetMapping(value = "/ticket")
    public String getTickets()   {

        return "ticket";

    }



}
