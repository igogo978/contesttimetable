package app.contestTimetable.service;

import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Job;
import app.contestTimetable.model.Location;
import app.contestTimetable.model.Team;
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

        //取出人数 以校为单位
        ArrayList<Team> teams = new ArrayList<>();
        contestconfig.getContestgroup().forEach(item -> {
            teamrepository.findByContestgroupContaining(item).forEach(team -> {
                teams.add(team);
            });
        });


        return job;
    }

}
