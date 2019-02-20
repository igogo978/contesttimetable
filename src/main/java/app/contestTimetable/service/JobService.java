package app.contestTimetable.service;

import app.contestTimetable.model.*;
import app.contestTimetable.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class JobService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TeamRepository teamrepository;

    @Autowired
    SchoolRepository schoolrepository;

    @Autowired
    JobRepository jobrepository;

    @Autowired
    LocationRepository locationrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;


    public Job getJob(Integer contestid, String action) {
        Job job = new Job();
        Boolean flag = Boolean.TRUE;

        //取出竞赛项目
        Contestconfig contestconfig = contestconfigrepository.findById(contestid).get();


        //取出场地
        ArrayList<Location> locations = new ArrayList<>();
        locationrepository.findAll().forEach(location -> locations.add(location));


        ArrayList<SchoolTeam> schoolteams = getSchoolteams(contestid);


        //分群, priorityorder为优先群，例如主场，已经拿到门票者
        //group1 大群组, group2 第二群组
        //分群
        List<SchoolTeam> teamgroup1 = new ArrayList<>();
        List<SchoolTeam> teamgroup2 = new ArrayList<>();
        StringBuilder locationorder = new StringBuilder();
        StringBuilder priorityorder = new StringBuilder();
        StringBuilder group1order = new StringBuilder();
        StringBuilder group2order = new StringBuilder();

        schoolteams.forEach(team -> {
            if (locationrepository.existsById(team.getSchoolid())) {
                //承办学校,已经拿到ticket的学校优先列入排序
                priorityorder.append(String.format("%s-", team.getSchoolid()));

            } else if (team.getMembers() > 4) {
                teamgroup1.add(team);
            } else {
                teamgroup2.add(team);
            }
        });


        String jobid = "";
        while (flag) {
            Collections.shuffle(locations);

            //打乱场地顺序
            locations.forEach(location -> {
                locationorder.append(String.format("%s-", location.getSchoolid()));
            });

            job.setLocationorder(locationorder.toString());

            //决定群组1的顺序
            Collections.shuffle(teamgroup1);

            //jobid是场地顺序加大群顺序的hash值, 指派工作后交给client, 为识别工作是否已派出,避免重复指派
            jobid = org.apache.commons.codec.digest.DigestUtils.sha256Hex(String.format("%s,%s", locationorder.toString(), group1order.toString()));

            //工作已派出,请client再次请求
            if (!jobrepository.existsById(jobid)) {
                flag = Boolean.FALSE;
                job.setJobid(jobid);
            }

        }


        teamgroup1.forEach(team -> {
            group1order.append(String.format("%s-", team.getSchoolid()));

        });

        job.setGroup1count(teamgroup1.size());


        teamgroup2.forEach(team -> {
            group2order.append(String.format("%s-", team.getSchoolid()));
        });

        job.setGroup2count(teamgroup2.size());

        job.setGroup1order(String.format("%s%s", priorityorder.toString(), group1order.toString()));
        job.setGroup2order(group2order.toString());

        job.setCalculatejob(contestconfig.getCalculatejob());

        if (action.equals("true")) {
            jobrepository.save(job);
        }

        return job;
    }

    public ArrayList<SchoolTeam> getSchoolteams(Integer id) {
        ArrayList<SchoolTeam> schoolteams = new ArrayList<>();

        //取出竞赛项目
        logger.info("jobid:" + String.valueOf(id));
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
//            logger.info(String.format("%s,%s", schoolname, isExist));

            if (isExist) {
                schoolteams.forEach(schoolteam -> {

                    if (schoolteam.getSchoolname().equals(schoolname)) {
                        schoolteam.setMembers(schoolteam.getMembers() + 1);
                    }
                });

            } else {
                SchoolTeam schoolteam = new SchoolTeam();
                School school = schoolrepository.findBySchoolname(team.getSchoolname());
//                logger.info(school.getSchoolid());
                schoolteam.setSchoolid(school.getSchoolid());
                schoolteam.setMembers(1);
                schoolteam.setSchoolname(schoolname);
                schoolteam.setContestgroup(String.join(",", contestconfig.getContestgroup()));
                schoolteams.add(schoolteam);

            }


        });

        return schoolteams;
    }

}
