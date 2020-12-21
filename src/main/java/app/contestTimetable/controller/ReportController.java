package app.contestTimetable.controller;

import app.contestTimetable.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReportController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ReportRepository reportrepository;


//    @GetMapping(value = "/report/{contestid}")
//    public String getReportContestid(Model model, @PathVariable("contestid") int contestid) {
////        ArrayList<Report> reports = new ArrayList<>();
////        reports = reportrepository.findByContestid(contestid);
//
//        model.addAttribute("contestid", contestid);
//
//        return "reportcontestid";
//
//    }

    @GetMapping(value = "/report")
    public String getReport() {

        return "report";

    }



    @GetMapping(value = "/report/uuid/{uuid}")
    public String getReportUuid(Model model, @PathVariable("uuid") String uuid) {
        model.addAttribute("uuid", uuid);

        return "reportuuid";

    }


    @GetMapping(value = "/report/restore")
    public String reportRestore() {

        return "reportrestore";

    }






}
