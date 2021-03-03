package app.contestTimetable.api;


import app.contestTimetable.model.Team;
import app.contestTimetable.repository.TeamRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

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
            teamRepository.findAll(Sort.by("schoolname").and(Sort.by("description"))).forEach(team -> {
                team.setAccount("*****");
                team.setPasswd("*****");
                teams.add(team);

            });
        } else {
            logger.info(request.getSession().getAttribute("login").toString());
            return teamRepository.findAll(Sort.by("schoolname").and(Sort.by("description")));

        }

        return teams;
    }

    @GetMapping(value = "/api/team/download")
    public ResponseEntity<Resource> getTeamJson(HttpServletRequest request) throws IOException {

        List<Team> teams = getTeam(request);
        String filename = "teams.json";
        //直接輸出
        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resourceStream, teams);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s", filename));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);

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

    @PostMapping(value = "/api/team/archive")
    public String getTeamToArchive(@RequestBody String payload) throws JsonProcessingException {
        logger.info(payload);
        return payload;
    }


    @PostMapping(value = "/api/team/contestitem")
    public Map<String, List<Team>> getTeamContestitem(@RequestBody String payload) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payload);
        String[] querystr = root.get("items").asText().split(" ");
        Map<String, List<Team>> teamsMap = new HashMap<>();
        Arrays.asList(querystr).forEach(item -> {
            List<Team> teams = new ArrayList<>();
            teamsMap.computeIfAbsent(item, k -> teams);
            teamRepository.findByContestitemContainingOrderByLocationDesc(item.toUpperCase()).forEach(teams::add);

        });

        logger.info(mapper.writeValueAsString(teamsMap));

        return teamsMap;
    }


}
