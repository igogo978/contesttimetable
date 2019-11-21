package app.contestTimetable.controller;

import app.contestTimetable.model.Job;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class JobController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    JobService jobservice;

    @Autowired
    ReportRepository reportrepository;

    @GetMapping(value = "/job/run")
    public String runReport() {

        return "jobrun";

    }


}
