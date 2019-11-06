package app.contestTimetable;


import app.contestTimetable.model.Team;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.SchoolTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamController {


    @Autowired
    TeamRepository teamRepository;

    @GetMapping(value = "/team")
    public List<Team> getTeam() {

        return teamRepository.findAllByOrderBySchoolname();
    }
}
