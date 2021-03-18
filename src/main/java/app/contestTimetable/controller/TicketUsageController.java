package app.contestTimetable.controller;


import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TicketUsageController {

    @GetMapping(value = "/ticket/usage")
    public String getTickets()   {

        return "ticketusage";

    }


}
