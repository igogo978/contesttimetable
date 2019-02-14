package app.contestTimetable.controller;

import app.contestTimetable.model.Job;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.SchoolTeam;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.service.JobService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class JobController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    JobService jobservice;

    @Autowired
    ReportRepository reportrepository;

    @GetMapping(value = "/job/{id}")
    public Job getId(@PathVariable("id") int id) {
        return jobservice.getJob(Integer.valueOf(id));
    }


    @GetMapping(value = "/job/{id}/schoolteam")
    public ArrayList<SchoolTeam> getSchoolTeam(@PathVariable("id") int id) {
        ArrayList<SchoolTeam> schoolteams = jobservice.getSchoolteams(Integer.valueOf(id));
        return schoolteams;
    }


    @GetMapping(value = "/job/{id}/report/{uuid}")
    public Report getReport(@PathVariable("id") int id, @PathVariable("uuid") String uuid) {

        logger.info("uuid:" + uuid);

        Report report = new Report();

        if (reportrepository.countByUuid(uuid) != 0) {
            report = reportrepository.findByUuid(uuid);
        }
        return report;
    }

    @PostMapping(value = "/job/{id}/report/{uuid}")
    public Report postReport(@PathVariable("id") int id, @RequestBody String payload) throws IOException {

        Report report = new Report();
        logger.info("update report:" + payload);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(payload);
        report.setUuid(node.get("uuid").asText());
//        logger.info(mapper.writeValueAsString(node.get("candidateList")));
        report.setReport(mapper.writeValueAsString(node.get("candidateList")));
        report.setDistance(node.get("totaldistance").asDouble());
        report.setContestid(Integer.valueOf(id));
        reportrepository.save(report);

        return report;
    }
}
