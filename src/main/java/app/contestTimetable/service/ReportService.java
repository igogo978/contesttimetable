package app.contestTimetable.service;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.Selectedreport;
import app.contestTimetable.model.Team;
import app.contestTimetable.repository.*;
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
public class ReportService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SelectedreportRepository selectedreportrepository;

    @Autowired
    ReportRepository reportrepository;

    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    TeamRepository teamrepository;

    public Boolean isExistUuid(String uuid) {

        if (reportrepository.countByUuid(uuid) == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }


    public void updateTeamLocation(Report report) throws IOException {
        //update team's location
        ArrayList<Team> teams = new ArrayList<>();
//        Selectedreport selectedreport = selectedreportrepository.findByContestid(report.getContestid());

        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(selectedreport.getReport());
        JsonNode root = mapper.readTree(report.getReport());

        Contestconfig config = contestconfigrepository.findById(1).get();
        List<String> contestgroup = config.getContestgroup();
        contestgroup.forEach(groupname -> {
            teamrepository.findByContestgroupContaining(groupname.toUpperCase()).forEach(team -> teams.add(team));
        });


        root.forEach(node -> {
            String location = node.get("location").get("name").asText();
//            logger.info(node.get("location").get("name").asText());
            node.get("teams").forEach(school -> {
//                logger.info(String.format("%s,%s",school.get("name").asText(),location));
                teams.forEach(team -> {
                    if (team.getSchoolname().equals(school.get("name").asText())) {
                        team.setLocation(location);
//                        logger.info("save location for team");
                        teamrepository.save(team);
                    }
                });

            });
        });


    }


}
