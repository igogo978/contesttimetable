package app.contestTimetable.api;


import app.contestTimetable.model.Team;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.ArchiveTeamService;
import app.contestTimetable.service.TeamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
public class TeamAPIController {

    Logger logger = LoggerFactory.getLogger(TeamAPIController.class);
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ArchiveTeamService archiveTeamService;

    @Autowired
    TeamService teamService;

    @GetMapping(value = "/api/team")
    public List<Team> getTeam(@RequestParam(defaultValue = "false") String passwd) {
        final List<Team> teams = new ArrayList<>();
        if (!passwd.equals("23952340")) {
            teamRepository.findAll(Sort.by("schoolname").and(Sort.by("description"))).forEach(team -> {
                team.setAccount("*****");
                team.setPasswd("*****");
                teams.add(team);

            });
        } else {
            logger.info("get correct passwd");
            return teamRepository.findAll(Sort.by("schoolname").and(Sort.by("description")));

        }

        return teams;
    }

    @PostMapping(value = "/api/team/download")
    public ResponseEntity<Resource> getTeamJson(@RequestBody String payload) throws IOException {

        JsonNode root = mapper.readTree(payload);
        String passwd = root.get("passwd").asText();

        List<Team> teams = getTeam(passwd);
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
        JsonNode root = mapper.readTree(payload);
        if (root.get("passwd").asText().equals("23952340")) {
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

    @GetMapping(value = "/api/team/archive/{year}")
    public Integer checkArchiveYear(@PathVariable Integer year) {
        return archiveTeamService.getArchiveYear(year);
    }

    @PostMapping(value = "/api/team/archive")
    public String updateTeamToArchive(@RequestBody String payload) throws IOException {
        JsonNode root = mapper.readTree(payload);
        int year = root.get("data").get("action").asInt();

        archiveTeamService.update(year);
        return root.get("data").get("action").asText();
    }





}
