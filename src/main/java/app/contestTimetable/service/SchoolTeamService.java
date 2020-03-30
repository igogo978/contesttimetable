package app.contestTimetable.service;

import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.School;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.school.ContestItem;
import app.contestTimetable.model.school.Contestid;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.SchoolRepository;
import app.contestTimetable.repository.SchoolTeamRepository;
import app.contestTimetable.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolTeamService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SchoolRepository schoolRepository;

    public List<SchoolTeam> getSchoolTeams() {


        return schoolTeamRepository.findAllByOrderByMembersDesc();
    }


    //get all schoolteam's area
    public List<String> getSchoolTeamsArea() {
        List<String> schoolteamAreas = new ArrayList<>();
        List<Team> teams = teamRepository.findAllByOrderBySchoolname();
        List<String> lists = new ArrayList<>();
        teams.forEach(team -> {
            String area = team.getSchoolname().split("(?<=區)")[0];
            lists.add(area);
        });
        schoolteamAreas = lists.stream().distinct().collect(Collectors.toList());
//        schoolteamAreas.forEach(startarea -> {
//
//        });




        return schoolteamAreas;
    }


    public void updateSchoolTeam() {
        schoolTeamRepository.deleteAll();

        //取出参赛学校
        List<Team> teams = teamRepository.findAllByOrderBySchoolname();
        List<SchoolTeam> schoolTeams = getSchoolTeams(teams);

        //取出每一所学校各个竞赛项目的人员数
        ArrayList<Contestconfig> contestconfigs = new ArrayList<>();
        contestconfigrepository.findAll().forEach(contestconfig -> {
            contestconfigs.add(contestconfig);
        });


        schoolTeams.forEach(schoolTeam -> {
            schoolTeam.setMembers(0);

            contestconfigs.forEach(contestconfig -> {
                //计算学校每一场的总人数
                Contestid contestid = new Contestid();
                contestid.setMembers(0);
                contestid.setContestid(contestconfig.getId());

                //处理每一场的单一竞赛项目人数
                contestconfig.getContestgroup().forEach(item -> {
                    int members = teamRepository.countByContestitemContainingAndSchoolname(item.toUpperCase(), schoolTeam.getSchoolname());
                    int presentationMembers = teamRepository.countByMembernameNotNullAndContestitemContainingAndSchoolname(item.toUpperCase(), schoolTeam.getSchoolname());

//                    简报可能两人一组
                    if (presentationMembers != 0) {
//                        System.out.println(String.format("%s,%s:%s", schoolTeam.getSchoolname(), item, presentationMembers));

                        members = members + presentationMembers;
                    }
                    ContestItem contestitem = new ContestItem();
                    contestitem.setMembers(members);
                    contestitem.setItem(item.toUpperCase());
                    contestitem.setContestid(contestconfig.getId());
                    schoolTeam.getContestitems().add(contestitem);

                    contestid.setMembers(contestid.getMembers() + members);
                });


                schoolTeam.getContestids().add(contestid);
                schoolTeam.setMembers(schoolTeam.getMembers() + contestid.getMembers());

            });
            schoolTeamRepository.save(schoolTeam);

        });


    }


    //取得所有参赛学校名单
    private List<SchoolTeam> getSchoolTeams(List<Team> teams) {
        List<SchoolTeam> schoolTeams = new ArrayList<>();
        teams.forEach(team -> {
            logger.info(team.getSchoolname());
            Boolean isExist = schoolTeams.stream().anyMatch(schoolTeam ->
                    schoolTeam.getSchoolname().equals(team.getSchoolname()));

            if (!isExist) {
                SchoolTeam schoolteam = new SchoolTeam();
                School school = schoolRepository.findBySchoolname(team.getSchoolname());
                schoolteam.setSchoolname(team.getSchoolname());
                schoolteam.setSchoolid(school.getSchoolid());
                schoolTeams.add(schoolteam);

            }

        });
        return schoolTeams;
    }


}
