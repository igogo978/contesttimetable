package app.contestTimetable.service;


import app.contestTimetable.model.*;
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

    public ArrayList<String> getReport(Report report) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(report.getReport()));
        ArrayList<String> teams = new ArrayList<>();
        String contestid = String.format("contestid,%s,   ",report.getContestid());
        teams.add(contestid);
        JsonNode root = mapper.readTree(report.getReport());
        root.forEach(location -> {
            String locationname = String.format("%s,%s,%s", location.get("location").get("schoolid").asText(), location.get("location").get("name").asText(), location.get("location").get("capacity").asInt());
            teams.add(String.format("%s", locationname));
            location.get("teams").forEach(school -> {
//                logger.info(school.get("name").asText());
                SchoolTeam schoolteam = new SchoolTeam();
                schoolteam.setSchoolname(school.get("name").asText());
                schoolteam.setSchoolid(school.get("schoolid").asText());
                schoolteam.setMembers(school.get("members").asInt());
                schoolteam.setDistance(school.get("distance").asDouble());

                String team = String.format("%s,%s,%s,%s,%s", "-", schoolteam.getSchoolid(), schoolteam.getSchoolname(), schoolteam.getMembers(), Integer.valueOf(schoolteam.getDistance().intValue()));
                teams.add(team);
            });

        });

        return teams;


    }


}
