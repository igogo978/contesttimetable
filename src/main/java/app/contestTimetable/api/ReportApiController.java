package app.contestTimetable.api;

import app.contestTimetable.model.Report;
import app.contestTimetable.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ReportApiController {

    @Autowired
    ReportRepository reportrepository;


    @GetMapping(value = "/api/report/{contestid}")
    public ArrayList<Report> getReport(@PathVariable("contestid") int contestid) {
        ArrayList<Report> reports = new ArrayList<>();
        reports = reportrepository.findByContestid(contestid);

        return reports;

    }
}
