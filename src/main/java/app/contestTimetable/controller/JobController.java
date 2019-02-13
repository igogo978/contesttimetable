package app.contestTimetable.controller;

import app.contestTimetable.model.Job;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.SchoolTeam;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
