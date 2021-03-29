package app.contestTimetable.service;


import app.contestTimetable.model.Team;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.TeamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    XlsxService readxlsx;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SchoolTeamService schoolTeamService;


    @Autowired
    ContestconfigRepository contestconfigRepository;

    @Autowired
    LocationRepository locationRepository;

    @Value("${multipart.location}")
    private Path path;

    public void restore(MultipartFile multipartFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
        Team[] teams = mapper.readValue(content, Team[].class);
//        empty team database
        teamRepository.deleteAll();
        Arrays.asList(teams).forEach(team -> teamRepository.save(team));

        logger.info("统计每间学校各项目参赛人数");
        schoolTeamService.updateSchoolTeam();
    }

    public void update() throws IOException {
        updateTeam();
        //统计每间学校各项目参赛人数
        logger.info("统计每间学校各项目参赛人数");
        schoolTeamService.updateSchoolTeam();
    }

    public void updateTeam() throws IOException {
        List<Team> teams = new ArrayList<>();
        teams = readxlsx.getTeams(path);

        //empty old records
        teamRepository.deleteAll();

        teams.forEach(team -> {
            teamRepository.save(team);
        });

        //update contest data in team.description
        contestconfigRepository.findAll().forEach(contestconfig -> {
            contestconfig.getContestgroup().forEach(contestitem -> {

                        List<Team> contestitemTeams = teamRepository.findByContestitemContaining(contestitem.toUpperCase());

                        if (contestitemTeams.size() == 0) {
                            logger.info(contestitem);
                        }
                        contestitemTeams.forEach(team -> {
                            team.setDescription(contestconfig.getId() + "-" + contestconfig.getDescription());
                        });
                    }
            );
        });

    }

    public List<Team> getTeamsByLocationAndContestitemContaining(Boolean isLogin, String locationname, String contestitem) {
        List<Team> teams = new ArrayList<>();
        teamRepository.findByLocationAndContestitemContaining(locationname, contestitem.toUpperCase()).forEach(team -> {
            if (isLogin == Boolean.FALSE) {
                team.setAccount("*****");
                team.setPasswd("*****");
            }
            team.setDescription(team.getDescription().substring(2));

            teams.add(team);
        });

        return teams;

    }

    public List<Team> getTeamsByLocation() {
        List<Team> teams = new ArrayList<>();
        teamRepository.findAll(Sort.by("location").and(Sort.by("description")).and(Sort.by("contestitem").and(Sort.by("schoolname")))).forEach(team -> {
            team.setAccount("");
            team.setPasswd("");
            teams.add(team);
        });
        return teams;
    }

    public List<Team> getTeamsByPlayer() {
        List<Team> teams = new ArrayList<>();
        teamRepository.findAll(Sort.by("location").and(Sort.by("schoolname")).and(Sort.by("description").and(Sort.by("contestitem")))).forEach(team -> {
            team.setAccount("");
            team.setPasswd("");
            team.setDescription(team.getDescription().substring(2));
            teams.add(team);
        });
        return teams;
    }

}
