package app.contestTimetable.service;


import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.school.Contestid;
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
}