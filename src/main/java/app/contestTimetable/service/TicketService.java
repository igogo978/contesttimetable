package app.contestTimetable.service;


import app.contestTimetable.model.Report;
import app.contestTimetable.model.Ticket;
import app.contestTimetable.repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class TicketService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TicketRepository ticketrepository;

    public ArrayList<Ticket> getAll() {

        ArrayList<Ticket> tickets = new ArrayList<>();

        ticketrepository.findAll().forEach(school -> tickets.add(school));

        return tickets;
    }


    public void updateTicket(Report report) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(report.getReport());

        root.forEach(candidate -> {
            String locationid = candidate.get("location").get("schoolid").asText();
            JsonNode node = candidate.get("teams");
            node.forEach(school -> {
                String schoolid = school.get("schoolid").asText();

                if (ticketrepository.countBySchoolid(schoolid) == 0) {
                    logger.info(String.format("schoolid: %s  update ticket", schoolid));
                    Ticket ticket = new Ticket();
                    ticket.setLocation(locationid);
                    ticket.setSchoolid(schoolid);

                    ticketrepository.save(ticket);

                }


            });
        });

    }
}
