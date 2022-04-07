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
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    XlsxService xlsxService;

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

    public void deleteTeamAndSchoolTeam() {
        teamRepository.deleteAll();
        schoolTeamService.delete();
    }

    public void restore(MultipartFile multipartFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
        Team[] teams = mapper.readValue(content, Team[].class);
//        empty team database
        teamRepository.deleteAll();
        Arrays.asList(teams).forEach(team -> teamRepository.save(team));

        logger.info("统计每间学校各项目参赛人数");
        schoolTeamService.updateSchoolTeam(List.of(teams));
    }

    public void updateTeamAndSchoolTeam() throws IOException {
        List<Team> teams = update();
        //统计每间学校各项目参赛人数
        logger.info("统计每间学校各项目参赛人数");
        schoolTeamService.updateSchoolTeam(teams);
    }

    public List<Team> update() throws IOException {
        List<Team> teams = new ArrayList<>();
        teams = xlsxService.getTeams(path);

        teams.forEach(team -> {
            if (team.getSchoolname().contains("市立光復國中(小)")) {
                team.setSchoolname("霧峰區光復國中小");
            }

            if (team.getSchoolname().contains("西屯區麗")) {
                team.setSchoolname("西屯區麗喆中小學");
            }

        });

        //empty old records
        teamRepository.deleteAll();

        teams.forEach(team -> {
            team = updateDescription(team);
            teamRepository.save(team);
        });

        return teams;
    }

    private Team updateDescription(Team team) {

        //update contest data in team.description
        contestconfigRepository.findAll().forEach(contestconfig -> {
            contestconfig.getContestgroup().forEach(contestitem -> {
                if (team.getContestitem().toUpperCase(Locale.ROOT).contains(contestitem.toUpperCase(Locale.ROOT))) {

                    team.setDescription(contestconfig.getId() + "-" + contestconfig.getDescription());
                }
            });

        });


        return team;
    }

    public List<Team> getTeamsByLocationAndContestitemContaining(Boolean isVisiblePasswd, String locationname, String contestitem) {
        List<Team> teams = teamRepository.findAll(Sort.by(Sort.Direction.DESC, "Schoolname")).stream()
                .filter(team -> team.getLocation().equals(locationname))
                .filter(team -> team.getContestitem().contains(contestitem))
                .collect(Collectors.toList());

//        teamRepository.findByLocationAndContestitemContaining(locationname, contestitem.toUpperCase()).forEach(team -> {
//            if (isVisiblePasswd == false) {
//                team.setAccount("*****");
//                team.setPasswd("*****");
//            }
//            team.setDescription(team.getDescription().substring(2));
//
//            teams.add(team);
//        });

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
