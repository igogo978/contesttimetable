package app.contestTimetable.api;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.Team;
import app.contestTimetable.service.SelectedReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class SelectedReportApiController {

    @Autowired
    SelectedReportService selectedreportservice;

    @GetMapping(value = "/report/selected/byteam/{contestid}")
    public ArrayList<Team> getSelectedReportsByTeam(@PathVariable("contestid") int contestid) throws IOException {


        return selectedreportservice.selectedReportByteam(contestid);

    }
}
