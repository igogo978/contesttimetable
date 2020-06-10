package app.contestTimetable.api;


import app.contestTimetable.model.school.Contestid;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.service.SchoolTeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SchoolteamController {


    @Autowired
    SchoolTeamService schoolteamservice;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping(value = "/api/schoolteam")
    public List<SchoolTeam> getSchoolteam() {

        return schoolteamservice.getSchoolTeams();
    }


    @GetMapping(value = "/api/schoolteam/area")
    public List<Map<String, Map<Integer, Integer>>> getSchoolteamByArea() {

        return schoolteamservice.getSchoolTeamsArea();
    }


}
