package app.contestTimetable.controller;

import app.contestTimetable.model.Report;
import app.contestTimetable.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Controller
public class ReportController {

    @Autowired
    ReportRepository reportrepository;


    @GetMapping(value = "/report/{contestid}")
    public String getReportContestid(Model model, @PathVariable("contestid") int contestid) {
//        ArrayList<Report> reports = new ArrayList<>();
//        reports = reportrepository.findByContestid(contestid);

        model.addAttribute("contestid", contestid);

        return "reportcontestid";

    }

    @GetMapping(value = "/report/uuid/{uuid}")
    public String getReportUuid(Model model, @PathVariable("uuid") String uuid) {
        model.addAttribute("uuid", uuid);

        return "reportuuid";

    }


}
