package app.contestTimetable.api;


import app.contestTimetable.model.Team;
import app.contestTimetable.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamAPIController {

    @Autowired
    TeamRepository teamRepository;

    @GetMapping(value = "/api/team")
    public List<Team> getTeam() {

        return teamRepository.findAllByOrderBySchoolname();
    }
}
