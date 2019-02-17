package app.contestTimetable.controller;

import app.contestTimetable.model.Report;
import app.contestTimetable.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Controller
public class ReportController {

    @Autowired
    ReportRepository reportrepository;


    @GetMapping(value = "/report/{contestid}")
    public String getReportContestid(@PathVariable("contestid") int contestid) {
//        ArrayList<Report> reports = new ArrayList<>();
//        reports = reportrepository.findByContestid(contestid);

        return "reportcontestid";

    }



}
