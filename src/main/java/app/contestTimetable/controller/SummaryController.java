package app.contestTimetable.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SummaryController {
    @GetMapping(value = "/teamsummaryschool")
    public String getSchoolTeamPage() {

        return "teamsummaryschool";
    }


    @GetMapping(value = "/teamsummaryarea")
    public String getSummaryTeamByAreaPage() {

        return "teamsummaryarea";
    }


}
