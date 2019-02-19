package app.contestTimetable.api;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.Selectedreport;
import app.contestTimetable.model.Ticket;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.repository.SelectedreportRepository;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.ReportService;
import app.contestTimetable.service.TicketService;
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
    SelectedreportRepository selectedreportrepository;

    @Autowired
    ReportService reportservice;

    @Autowired
    TicketService ticketservice;


    @GetMapping(value = "/api/report/{contestid}")
    public ArrayList<Report> getReports(@PathVariable("contestid") int contestid) {
        ArrayList<Report> reports = new ArrayList<>();
        reports = reportrepository.findTop10ByContestidOrderByDistanceAsc(contestid);


        return reports;

    }

    @GetMapping(value = "/api/report/uuid/{uuid}")
    public Report getReport(@PathVariable("uuid") String uuid) {

        return reportrepository.findByUuid(uuid);

    }


    @GetMapping(value = "/report/{contestid}/lock/{uuid}")
    public String lockReportInContestid(@PathVariable("contestid") int contestid, @PathVariable("uuid") String uuid) throws IOException {
        String response = null;
        Report report = new Report();
        if (reportservice.isExistUuid(uuid)) {
            report = reportrepository.findByUuid(uuid);
//            reportservice.updateTicket(report);
            ticketrepository.deleteAll();
//            response = reportservice.updateTicket(report);
            response = report.getReport();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            //contesid==1, update ticket
            if (contestid == 1) {
                ticketservice.updateTicket(report);

//                root.forEach(candidate -> {
//                    String locationid = candidate.get("location").get("schoolid").asText();
//                    JsonNode node = candidate.get("teams");
//                    node.forEach(school -> {
//                        String teamschoolid = school.get("schoolid").asText();
//                        Ticket ticket = new Ticket();
//                        ticket.setLocation(locationid);
//                        ticket.setSchoolid(teamschoolid);
//
//                        ticketrepository.save(ticket);
//
//                    });
//                });

            }

            //update selected report
            if (selectedreportrepository.countByContestid(contestid) == 0) {
                Selectedreport selectedreport = new Selectedreport();
                selectedreport.setContestid(contestid);
                selectedreport.setReport(report.getReport());
                selectedreport.setDistance(report.getDistance());

                selectedreportrepository.save(selectedreport);
            }

            //update team's location
            logger.info("update team location");
            reportservice.updateTeamLocation(report);



        } else {
            response = "no uuid records";
        }


        return response;

    }
}
