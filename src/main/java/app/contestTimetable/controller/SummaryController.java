package app.contestTimetable.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SummaryController {
    @GetMapping(value = "/team/summary/school")
    public String getSchoolTeamPage() {

        return "m";
    }


    @GetMapping(value = "/team/summary/area")
    public String getSummaryTeamByAreaPage() {

        return "teamsummaryarea";
    }


}
