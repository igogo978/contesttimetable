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
public class IndexController {

    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping(value = "/")
    public String getId() {

        return "index";
    }


}
