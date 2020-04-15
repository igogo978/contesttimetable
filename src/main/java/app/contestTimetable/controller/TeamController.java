package app.contestTimetable.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeamController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping(value = "/team")
    public String getTeamPage() {

        return "team";
    }


    @GetMapping(value = "/team/contestitem")
    public String getTeamContestitemPage() {

        return "teamcontestitem";
    }

    @GetMapping(value = "/schoolteam")
    public String getSchoolTeamPage() {

        return "schoolteam";
    }


}
