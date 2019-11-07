package app.contestTimetable.api;


import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.service.SchoolTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchoolteamController {


    @Autowired
    SchoolTeamService schoolteamservice;

    @GetMapping(value = "/api/schoolteam")
    public List<SchoolTeam> getSchoolteam() {

        return schoolteamservice.getSchoolTeams();
    }
}
