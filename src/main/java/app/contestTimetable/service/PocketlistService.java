package app.contestTimetable.service;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.school.Contestid;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.PocketlistRepository;
import app.contestTimetable.repository.TeamRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PocketlistService {


    @Autowired
    PocketlistRepository pocketlistRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ContestconfigRepository contestconfigRepository;


    @Autowired
    LocationRepository locationRepository;


    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void updatePocketlist(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode items = mapper.readTree(payload);
        pocketlistRepository.deleteAll();

        items.forEach(node -> {
            JsonNode location = node.get("location");
            JsonNode item = node.get("teams");
            item.forEach(team -> {

                Pocketlist pocketlist = new Pocketlist();

                //location
                pocketlist.setLocationid(location.get("schoolid").asText());
                pocketlist.setLocationname(location.get("name").asText());
                pocketlist.setCapacity(location.get("capacity").asInt());

                location.get("contestids").forEach(contest -> {
//                    logger.info(contest.get("contestid").asText());
                    Contestid contestid = new Contestid();
                    contestid.setContestid(contest.get("contestid").asInt());
                    contestid.setMembers(contest.get("members").asInt());
                    pocketlist.getLocationcontestids().add(contestid);
                });


                pocketlist.setSchoolid(team.get("schoolid").asText());
                pocketlist.setSchoolname(team.get("name").asText());
//                logger.info(team.get("name").asText()+":"+team.get("scores").asInt());
                pocketlist.setDescription(team.get("scores").asText());
                JsonNode contestidsNode = team.get("contestids");
                contestidsNode.forEach(contest -> {
                    Contestid contestid = new Contestid();
                    contestid.setContestid(contest.get("contestid").asInt());
                    contestid.setMembers(contest.get("members").asInt());
                    pocketlist.getTeamcontestids().add(contestid);

//                    logger.info(contest.get("contestid").asText()+":"+contest.get("members").asInt());

                });
                pocketlistRepository.save(pocketlist);

            });


        });

        //            更新team的location 1015
        logger.info("update teams' location");

        List<Pocketlist> pocketlist = new ArrayList<>();
        pocketlistRepository.findAll().forEach(pocketlist::add);

        pocketlist.forEach(pocketitem -> {
//                logger.info("学校：" + pocketitem.getSchoolname() + "in" + pocketitem.getLocationname());

            List<Team> teams = teamRepository.findBySchoolname(pocketitem.getSchoolname());
            teams.forEach(team -> {
                team.setLocation(pocketitem.getLocationname());
                teamRepository.save(team);
            });

        });


    }

    public List<Team> getPocketlistByPlayer() {
        List<Team> teams = new ArrayList<>();


        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        List<Contestconfig> contestconfigs = contestconfigRepository.findAllByOrderByIdAsc();

        locations.forEach(location -> {
            List<String> schools = new ArrayList<>();
            schools = teamRepository.findDistinctSchoolnameByLocation(location.getLocationname());
            schools.forEach(schoolname -> {
                contestconfigs.forEach(contestconfig -> {
                    contestconfig.getContestgroup().forEach(contestitem -> {
                        logger.info(String.format("%s,%s,%s", location.getLocationname(), schoolname, contestitem));
                        teamRepository.findBySchoolnameAndContestitemContaining(schoolname, contestitem).forEach(teams::add);
                    });
                });
            });

        });


        return teams;
    }


    public List<Team> getPocketlistByLocation() {
        List<Team> teams = new ArrayList<>();

        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        List<Contestconfig> contestconfigs = contestconfigRepository.findAllByOrderByIdAsc();

        locations.forEach(location -> {
            final List<String> schools = teamRepository.findDistinctSchoolnameByLocation(location.getLocationname());

            contestconfigs.forEach(contestconfig -> {
                contestconfig.getContestgroup().forEach(contestitem -> {
                    schools.forEach(schoolname -> {
                        teamRepository.findBySchoolnameAndContestitemContaining(schoolname, contestitem).forEach(teams::add);

                    });
                });
            });

        });







        return teams;
    }


}
