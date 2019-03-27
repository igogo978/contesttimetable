package app.contestTimetable.service;


import app.contestTimetable.model.school.Location;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.school.SchoolTeam;
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
import java.util.List;

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

    public void updateTicket(List<Ticket> tickets) {
        logger.info("update tickets");
        ticketrepository.deleteAll();
        tickets.forEach(ticket -> {
            ticketrepository.save(ticket);
        });

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
                    ticket.setLocationid(locationid);
                    ticket.setSchoolid(schoolid);

                    ticketrepository.save(ticket);

                }


            });
        });

    }

    public void updateTicket(SchoolTeam schoolteam, Location location) throws IOException {
        if (ticketrepository.countBySchoolid(schoolteam.getSchoolid()) == 0) {
            Ticket ticket = new Ticket();
            ticket.setSchoolid(schoolteam.getSchoolid());
            ticket.setLocationid(location.getSchoolid());
//            logger.info("update ticket");
//            logger.info(String.format("%s,%s",schoolteam.getSchoolname(),location.getSchoolid()));
            ticketrepository.save(ticket);

        }

    }
}
