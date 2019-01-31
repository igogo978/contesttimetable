package app.contestTimetable.service;

import app.contestTimetable.model.*;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.SchoolRepository;
import app.contestTimetable.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class JobService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TeamRepository teamrepository;

    @Autowired
    SchoolRepository schoolrepository;

    @Autowired
    LocationRepository locationrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;


    public Job getJob(Integer id) {
        Job job = new Job();

        //取出竞赛项目
        Contestconfig contestconfig = contestconfigrepository.findById(id).get();


        //取出场地
        ArrayList<Location> locations = new ArrayList<>();
        locationrepository.findAll().forEach(location -> locations.add(location));

        //分群, priorityorder为优先群，例如主场，已经拿到门票者
        //group1 大群组, group2 第二群组
        StringBuilder priorityorder = new StringBuilder();
        StringBuilder group1order = new StringBuilder();
        StringBuilder group2order = new StringBuilder();


        return job;
    }

    public ArrayList<SchoolTeam> getSchoolteams(Integer id) {
        ArrayList<SchoolTeam> schoolteams = new ArrayList<>();

        //取出竞赛项目
        Contestconfig contestconfig = contestconfigrepository.findById(id).get();

        //取出人数 以校为单位
        ArrayList<Team> teams = new ArrayList<>();
        contestconfig.getContestgroup().forEach(item -> {
            teamrepository.findByContestgroupContaining(item).forEach(team -> {
                teams.add(team);
            });
        });

        teams.forEach(team -> {
            String schoolname = team.getSchoolname();

            Boolean isExist = schoolteams.stream().anyMatch(schoolTeam -> schoolTeam.getSchoolname().equals(schoolname));
            logger.info(String.format("%s,%s", schoolname, isExist));

            if (isExist) {
                schoolteams.forEach(schoolteam -> {

                    if (schoolteam.getSchoolname().equals(schoolname)) {
                        schoolteam.setMembers(schoolteam.getMembers() + 1);
                    }
                });

            } else {
                SchoolTeam schoolteam = new SchoolTeam();
                School school = schoolrepository.findBySchoolname(team.getSchoolname());
                logger.info(school.getSchoolid());
                schoolteam.setSchoolid(school.getSchoolid());
                schoolteam.setMembers(1);
                schoolteam.setSchoolname(schoolname);
                schoolteam.setContestgroup(String.join(",",contestconfig.getContestgroup()));
                schoolteams.add(schoolteam);

            }


        });

        return schoolteams;
    }

}
