package app.contestTimetable.api;


import app.contestTimetable.model.Team;
import app.contestTimetable.repository.TeamRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TeamAPIController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TeamRepository teamRepository;

    @GetMapping(value = "/api/team")
    public List<Team> getTeam(HttpServletRequest request) {
        final List<Team> teams = new ArrayList<>();
        if (request.getSession(false) == null) {
            logger.info("session null");
            teamRepository.findAllByOrderBySchoolname().forEach(team -> {
                team.setAccount("*****");
                team.setPasswd("*****");
                teams.add(team);
            });
        } else {
            logger.info(request.getSession().getAttribute("login").toString());
            return teamRepository.findAllByOrderBySchoolname();

        }

        return teams;
    }

    @PostMapping(value = "/api/team")
    public List<Team> getTeamAccountAndPasswd(@RequestBody String payload, HttpServletRequest request) throws JsonProcessingException {
        final List<Team> teams = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payload);
        if (root.get("passwd").asText().equals("23952340")) {
            logger.info("get session");
            request.getSession().setAttribute("login", "admin");
            return teamRepository.findAllByOrderBySchoolname();
        } else {
            request.getSession().invalidate();
            teamRepository.findAllByOrderBySchoolname().forEach(team -> {
                team.setAccount("*****");
                team.setPasswd("*****");
                teams.add(team);
            });
        }

        return teams;
    }

}
