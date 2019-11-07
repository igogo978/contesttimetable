package app.contestTimetable.api;


import app.contestTimetable.model.PocketPlayer;
import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.Team;
import app.contestTimetable.repository.PocketlistRepository;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.PocketlistService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PocketlistApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    PocketlistService pocketlistService;

    @Autowired
    PocketlistRepository pocketlistRepository;


    @Autowired
    TeamRepository teamRepository;

    @PostMapping(value = "/api/pocketlist")
    public String postReport(@RequestBody String payload) throws IOException {
        pocketlistService.updatePocketlist(payload);

        return payload;
    }

    @GetMapping(value = "/api/pocketlist")
    public List<Pocketlist> getReport() throws IOException {

        List<Pocketlist> lists = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(team->lists.add(team));
        pocketlistRepository.findAll().forEach(lists::add);

        return lists;
    }


    @GetMapping(value = "/api/pocket/player")
    public List<Team> getPocketListByPlayer() throws IOException {
//        List<Pocketlist> pocketlist = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(pocketlist::add);
        final List<Team> teams = new ArrayList<>();
        teamRepository.findAllByOrderBySchoolname().forEach(teams::add);

        return teams;
    }


    @GetMapping(value = "/api/pocket/location")
    public List<Team> getPocketListByLocation() throws IOException {
//        List<Pocketlist> pocketlist = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(pocketlist::add);
        final List<Team> teams = new ArrayList<>();
        teamRepository.findAllByOrderByLocation().forEach(teams::add);

        return teams;
    }


}
