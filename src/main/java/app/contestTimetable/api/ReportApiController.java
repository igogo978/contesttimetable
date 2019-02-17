package app.contestTimetable.api;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.Ticket;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.ReportService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ReportApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ReportRepository reportrepository;

    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    ReportService reportservice;


    @GetMapping(value = "/api/report/{contestid}")
    public ArrayList<Report> getReport(@PathVariable("contestid") int contestid) {
        ArrayList<Report> reports = new ArrayList<>();
        reports = reportrepository.findByContestid(contestid);


        return reports;

    }


    @GetMapping(value = "/report/{contestid}/lock/{uuid}")
    public String lockReportWithContestid(@PathVariable("contestid") int contestid, @PathVariable("uuid") String uuid) throws IOException {
        String response = null;
        Report report = new Report();
        if (reportservice.isExistUuid(uuid)) {
            report = reportrepository.findByUuid(uuid);
            reportservice.updateTicket(report);
            ticketrepository.deleteAll();
            response = reportservice.updateTicket(report);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            root.forEach(candidate -> {
                String locationid = candidate.get("location").get("schoolid").asText();
                JsonNode node = candidate.get("teams");
                node.forEach(school -> {
                    String teamschoolid = school.get("schoolid").asText();
                    Ticket ticket = new Ticket();
                    ticket.setLocation(locationid);
                    ticket.setSchoolid(teamschoolid);

                    ticketrepository.save(ticket);

                });
            });


        } else {
            response = "no uuid records";
        }


        return response;

    }
}
