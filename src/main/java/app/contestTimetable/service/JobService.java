package app.contestTimetable.service;

import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Job;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


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
    TicketRepository ticketrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

    @Autowired
    ReportService reportService;


//    public Job getFineJob() throws JsonProcessingException {
//        Job job = new Job();
//
//        //find lowest scores report
//        Report report = reportService.getReports().get(0);
//        logger.info(String.valueOf(report.getScores()));
//        ReportSerial serial = reportService.getReportserial(report.getUuid());
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode node = mapper.readTree(serial.getSerial());
//        job.setLocationorder(node.get("locationorder").asText());
//        job.setPriorityorder(node.get("priorityorder").asText());
//        job.setGroup1order(node.get("group1order").asText());
//        job.setGroup2order(node.get("group2order").asText());
//        job.setPrioritysize(node.get("priorityorder").asText().split("-").length);
//        job.setGroup1size(node.get("group1order").asText().split("-").length);
//        job.setGroup2size(node.get("group2order").asText().split("-").length);
//
//        return job;
//
//    }

    public Job getJob() {
        Contestconfig contestconfig = contestconfigrepository.findById(1).get();

        //四个场次一起算
        Job job = new Job();

        //取出场地
        List<Location> locations = new ArrayList<>();
        locationrepository.findAll().forEach(location -> locations.add(location));

        //参赛学校
        List<SchoolTeam> schoolTeams = schoolTeamRepository.findAllByOrderByMembersDesc();

        //分群, priorityorder为优先群，例如主场，已经拿到门票者
        //group1 大群组, group2 第二群组
        List<SchoolTeam> teamgroup1 = new ArrayList<>();
        List<SchoolTeam> teamgroup2 = new ArrayList<>();
        StringBuilder locationorder = new StringBuilder();
        StringBuilder priorityorder = new StringBuilder();
        StringBuilder group1order = new StringBuilder();
        StringBuilder group2order = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        AtomicInteger prioritysize = new AtomicInteger(0);
        schoolTeams.forEach(schoolTeam -> {

            //if team members > 4 in a day, this school is viewed as a big group
            int day1 = schoolTeam.getContestids().get(0).getMembers() + schoolTeam.getContestids().get(1).getMembers();
            int day2 = schoolTeam.getContestids().get(2).getMembers() + schoolTeam.getContestids().get(3).getMembers();

            if (locationrepository.existsById(schoolTeam.getSchoolid()) || ticketrepository.existsById(schoolTeam.getSchoolid())) {
                //承办学校或已经拿到ticket的学校优先列入排序
                priorityorder.append(String.format("%s-", schoolTeam.getSchoolid()));
                prioritysize.set(prioritysize.incrementAndGet());
            } else if (day1 > 4 || day2 > 4) {
                teamgroup1.add(schoolTeam);
            } else {
                teamgroup2.add(schoolTeam);
            }

        });

        job.setPriorityorder(priorityorder.toString());
        job.setPrioritysize(prioritysize.get());
        //打乱场地顺序
        Collections.shuffle(locations);
        locations.forEach(location -> {
            locationorder.append(String.format("%s-", location.getSchoolid()));
        });
        job.setLocationorder(locationorder.toString());

        //决定群组1的顺序
        Collections.shuffle(teamgroup1);
        teamgroup1.forEach(team -> {
            group1order.append(String.format("%s-", team.getSchoolid()));
        });
        job.setGroup1order(group1order.toString());

        //群组2直接列
        teamgroup2.forEach(team -> {
            group2order.append(String.format("%s-", team.getSchoolid()));
        });
        job.setGroup2order(group2order.toString());

        job.setGroup1size(teamgroup1.size());
        job.setGroup2size(teamgroup2.size());

        return job;
    }

//    public ArrayList<SchoolTeam> getSchoolteams(Integer id) {
//        ArrayList<SchoolTeam> schoolteams = new ArrayList<>();
//
//        //取出竞赛项目
//        logger.info("取出竞赛项目 jobid:" + String.valueOf(id));
//        Contestconfig contestconfig = contestconfigrepository.findById(id).get();
//
//        System.out.println("contest groupt" + contestconfig.getContestgroup().toString());
//        //取出人数 以校为单位
//        ArrayList<Team> teams = new ArrayList<>();
//        contestconfig.getContestgroup().forEach(item -> {
//            teamrepository.findByContestitemContaining(item).forEach(team -> {
//                teams.add(team);
//            });
//        });
//
//
//        teams.forEach(team -> {
//            String schoolname = team.getSchoolname();
//
//            Boolean isExist = schoolteams.stream().anyMatch(schoolTeam -> schoolTeam.getSchoolname().equals(schoolname));
////            logger.info(String.format("%s,%s", schoolname, isExist));
//            if (isExist) {
//                schoolteams.forEach(schoolteam -> {
//
//                    if (schoolteam.getSchoolname().equals(schoolname)) {
//                        schoolteam.setMembers(schoolteam.getMembers() + 1);
//                    }
//                });
//
//            } else {
//                SchoolTeam schoolteam = new SchoolTeam();
////                logger.info(team.getSchoolname());
//                School school = schoolrepository.findBySchoolname(team.getSchoolname());
////                logger.info(school.getSchoolid());
//                schoolteam.setSchoolid(school.getSchoolid());
//                schoolteam.setMembers(1);
//                schoolteam.setSchoolname(schoolname);
////                schoolteam.setContestgroup(String.join(",", contestconfig.getContestgroup()));
//                schoolteams.add(schoolteam);
//
//            }
//
//        });
//        return schoolteams;
//    }

}
