package app.contestTimetable.api;

import app.contestTimetable.model.Job;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.school.SchoolTeam;
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
public class JobApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    JobService jobservice;

    @Autowired
    ReportRepository reportrepository;

    @GetMapping(value = "/job")
    public Job getId() {

        return jobservice.getJob();
    }


}
