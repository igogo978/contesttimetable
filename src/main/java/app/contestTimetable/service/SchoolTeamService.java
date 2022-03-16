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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    public void delete(){
        schoolTeamRepository.deleteAll();
    }


    public List<SchoolTeam> getSchoolTeams() {
        return schoolTeamRepository.findAllByOrderByMembersDesc();
    }

    //get all schoolteam's area
    public List<String> getAreas() {
        List<String> areas = new ArrayList<>();
        List<Team> teams = teamRepository.findAllByOrderBySchoolname();
        List<String> lists = new ArrayList<>();
        teams.forEach(team -> {
            String area = team.getSchoolname().split("(?<=區)")[0];
            lists.add(area);
        });
        areas = lists.stream().distinct().collect(Collectors.toList());
        return areas;
    }


    public List<Map<String, Map<Integer, Integer>>> getSchoolTeamsArea() {

        List<String> areas = areas = getAreas();


        List<SchoolTeam> schoolTeams = new ArrayList<>();
//        Map<Integer, Integer> contestidSum = new HashMap<>();
//        Map<String, Map<Integer, Integer>> areasSummary = new HashMap<>();
        List<Map<String, Map<Integer, Integer>>> areasList = new ArrayList<>();
        areas.forEach(area -> {
            Map<String, Map<Integer, Integer>> areaSummary = new HashMap<>();
            List<SchoolTeam> areaSchools = schoolTeamRepository.findBySchoolnameContaining(area);
            Map<Integer, Integer> contestidSum = new HashMap<>();
            contestconfigrepository.findAllByOrderByIdAsc().forEach(contestconfig -> {
                if (!contestidSum.containsKey(contestconfig.getId())) {

                    contestidSum.put(contestconfig.getId(), 0);
                }
            });
            contestidSum.put(0, 0);
            areaSummary.put(area, contestidSum);

            areaSchools.forEach(schoolTeam -> {
                schoolTeam.getContestids().forEach(contestid -> {
                    switch (contestid.getContestid()) {
                        case 1:

                            contestidSum.put(1, contestidSum.get(1) + contestid.getMembers());
                            break;

                        case 2:

                            contestidSum.put(2, contestidSum.get(2) + contestid.getMembers());
                            break;

                        case 3:

                            contestidSum.put(3, contestidSum.get(3) + contestid.getMembers());
                            break;

                        case 4:

                            contestidSum.put(4, contestidSum.get(4) + contestid.getMembers());
                            break;
                        default:

                    }
                    contestidSum.put(0, contestid.getMembers() + contestidSum.get(0));
                });

            });

            areasList.add(areaSummary);
        });

        return areasList;
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

            Boolean isExist = schoolTeams.stream().anyMatch(schoolTeam ->
                    schoolTeam.getSchoolname().equals(team.getSchoolname()));

            if (!isExist) {
                SchoolTeam schoolteam = new SchoolTeam();
                logger.info(team.getSchoolname());
                School school = schoolRepository.findBySchoolname(team.getSchoolname());
                if (school == null) {

                    schoolteam.setSchoolid(team.getSchoolname());
                } else {
                    schoolteam.setSchoolid(school.getSchoolid().trim());
                }

                schoolteam.setSchoolname(team.getSchoolname());
                schoolTeams.add(schoolteam);

            }

        });
        return schoolTeams;
    }


//    public List<SchoolTeam> getSchoolteams(Integer id) {
//        List<SchoolTeam> schoolteams = new ArrayList<>();
//
//        //取出竞赛项目
//        logger.info("取出竞赛项目 jobid:" + String.valueOf(id));
//        Contestconfig contestconfig = contestconfigrepository.findById(id).get();
//
//        System.out.println("contest groupt" + contestconfig.getContestgroup().toString());
//        //取出人数 以校为单位
//        ArrayList<Team> teams = new ArrayList<>();
//        contestconfig.getContestgroup().forEach(item -> {
//            teamRepository.findByContestitemContaining(item).forEach(team -> {
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
//                School school = schoolRepository.findBySchoolname(team.getSchoolname());
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
