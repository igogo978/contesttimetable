package app.contestTimetable.service;


import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Pocketlist;
import app.contestTimetable.model.school.Contestid;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.PocketlistRepository;
import app.contestTimetable.repository.TeamRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    ObjectMapper mapper = new ObjectMapper();

    public void restorePocketlist(String pocketlistfile) throws IOException {

        logger.info("pocketlist file:" + pocketlistfile);
        ObjectMapper mapper = new ObjectMapper();
        if (new File(pocketlistfile).exists()) {
            Pocketlist[] lists = mapper.readValue(new File(pocketlistfile), Pocketlist[].class);
            Arrays.stream(lists).forEach(pocketlistitem -> {
                logger.info(String.format("%s,%s", pocketlistitem.getLocationname(), pocketlistitem.getSchoolname()));
                pocketlistRepository.save(pocketlistitem);

                //            更新team的location
                List<Team> teams = teamRepository.findBySchoolname(pocketlistitem.getSchoolname());
                if (teams.size() != 0) {
                    teams.forEach(team -> {
                        logger.info("update teams' location");

                        team.setLocation(pocketlistitem.getLocationname());
                        teamRepository.save(team);
                    });
                }


            });


        }


    }

    public void updatePocketlist(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();


        logger.info(payload);

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

        //empty records
        teamRepository.findAllByOrderByLocation().forEach(team -> {
            team.setLocation("");
            teamRepository.save(team);
        });

        List<Pocketlist> pocketlist = new ArrayList<>();
        pocketlistRepository.findAll().forEach(pocketlist::add);

        pocketlist.forEach(pocketitem -> {
//                logger.info("学校：" + pocketitem.getSchoolname() + "in" + pocketitem.getLocationname());

            List<Team> teams = teamRepository.findBySchoolname(pocketitem.getSchoolname());
            if (teams.size() != 0) {
                teams.forEach(team -> {
                    team.setLocation(pocketitem.getLocationname());

                    Location location = locationRepository.findByLocationname(pocketitem.getLocationname());
                    List<Map<String, String>> comments = new ArrayList<>();
                    Map<String, String> comment = new HashMap<>();
                    comment.put("color", location.getColor());
                    comments.add(comment);
                    try {
                        team.setComments(mapper.writeValueAsString(comments));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    teamRepository.save(team);
                });
            }

        });


    }

    public List<Team> getPocketlistByPlayer() {
        List<Team> teams = new ArrayList<>();
        teamRepository.findAll(Sort.by("location").and(Sort.by("schoolname")).and(Sort.by("description").and(Sort.by("contestitem")))).forEach(team -> {
            team.setAccount("");
            team.setPasswd("");
            team.setDescription(team.getDescription().split("-")[1]);
            teams.add(team);
        });

        return teams;
    }


    public List<Team> getPocketlistByLocation() {
        List<Team> teams = new ArrayList<>();
        teamRepository.findAll(Sort.by("location").and(Sort.by("description")).and(Sort.by("contestitem").and(Sort.by("schoolname")))).forEach(team -> {
                    team.setAccount("");
                    team.setPasswd("");
                    teams.add(team);
        });


        return teams;
    }


}
