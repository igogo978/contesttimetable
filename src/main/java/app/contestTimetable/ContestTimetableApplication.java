package app.contestTimetable;

import app.contestTimetable.repository.*;
import app.contestTimetable.service.XlsxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class ContestTimetableApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(ContestTimetableApplication.class);

    @Value("${multipart.location}")
    private Path path;

    @Autowired
    TeamRepository teamrepository;


    @Autowired
    SchoolRepository schoolrepository;

    @Autowired
    LocationRepository locationrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

    @Autowired
    XlsxService readxlsx;


    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ReportScoresSummaryRepository reportScoresSummaryRepository;

    public static void main(String[] args) {
        SpringApplication.run(ContestTimetableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Files.isDirectory(path)) {
            Files.createDirectory(path);
        }

//        String cwd = System.getProperty("user.dir");
//        String docPath = String.format("%s/docs", cwd);
//        String settingPath = String.format("%s/settings", cwd);


//        List<Team> teams = teamrepository.findAllByOrderBySchoolname();
//        if (teams.size() != 0) {
//            teams.forEach(team -> {
//                team.setAccount("");
//                team.setPasswd("");
//                teamrepository.save(team);
//            });
//
//        }


        //读取竞赛设定档
//        String contestconfigfile = String.format("%s/%s", settingPath, "contestconfig.json");
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root;
//
//        if (new File(contestconfigfile).isFile()) {
//            //create ObjectMapper instance
//            System.out.println("读取设定档");
//            root = mapper.readTree(new File(contestconfigfile));
//            Contestconfig contestconfig = new Contestconfig();
//            contestconfig.setCount(root.get("count").asInt());
//
//            JsonNode node = root.get("setting");
//
//
//            for (JsonNode subnode : node) {
//
//                Integer id = subnode.get("id").asInt();
//
//                String description = subnode.get("description").asText();
//                contestconfig.setId(id);
//                contestconfig.setDescription(description);
//
//
//                subnode.get("contestgroup").forEach(element -> {
//                    contestconfig.getContestgroup().add(element.asText());
//                });
//
//                contestconfigrepository.save(contestconfig);
//                contestconfig.getContestgroup().clear();
//            }
//        }


        //读取参赛队伍
//        teams = readxlsx.getTeams(docPath);
//
//        //empty all
//        teamrepository.deleteAll();
//
//
//        teams.forEach(team -> {
//            teamrepository.save(team);
//        });


//        //update contesttime in team.description
//        contestconfigrepository.findAll().forEach(contestconfig -> {
//            contestconfig.getContestgroup().forEach(contestgroup -> {
//                        teamrepository.findByContestitemContaining(contestgroup).forEach(team -> {
//                            team.setDescription(contestconfig.getDescription());
//                            teamrepository.save(team);
//                        });
//                    }
//            );
//        });


//        //读取台中市学校名单
//        String tcschool = String.format("%s/%s", settingPath, "tcschool.xlsx");
//
//        List<School> schools = new ArrayList<>();
//        schools = readxlsx.getSchools(tcschool);
//        ArrayList<School> tcschools = new ArrayList<>();
//
//
//        schools.forEach(school -> {
//
//            schoolrepository.save(school);
//        });


//        List<String> areas = new ArrayList<>();
//        schools.forEach(school -> {
////            logger.info(school.getSchoolname());
//            Pattern pattern = Pattern.compile("(.{1,3}區)");
//            Matcher matcher = pattern.matcher(school.getSchoolname());
//            if (matcher.find()) {
//                if (!areas.contains(matcher.group(0))) {
//                    areas.add(matcher.group(0));
//                }
//            }
//        });

//        areas.forEach(start->{
//            areas.forEach(end->{
//                System.out.println(String.format("%s,%s",start,end));
//            });
//        });


//        //读取场地
//        //empty location records
//        locationrepository.deleteAll();
//        System.out.println("设定场地资料");
//        String locationfile = String.format("%s/%s", settingPath, "location.xlsx");
//        ArrayList<Location> locations = new ArrayList<>();
//        locations = readxlsx.getLocations(locationfile);
//
//        Location pending = new Location();
//        pending.setLocationname("未知");
//        pending.setSchoolid("999999");
//        pending.setCapacity(999);
//
//        locations.add(pending);
//
//        locations.forEach(location -> {
//            School school = schoolrepository.findBySchoolname(location.getLocationname());
//            location.setSchoolid(school.getSchoolid());
//            contestconfigrepository.findAll().forEach(contestconfig -> {
//                Contestid contestid = new Contestid();
//                contestid.setContestid(contestconfig.getId());
////                System.out.println(contestconfig.getId());
//                contestid.setMembers(location.getCapacity());
//                location.getContestids().add(contestid);
//            });
//
//        });
////
////
//        //存入location
//        locations.forEach(location -> {
//            locationrepository.save(location);
//        });


//        //取出参赛学校
//        List<Team> teams = new ArrayList<>();
//        List<SchoolTeam> schoolTeams = new ArrayList<>();
//        schoolTeams = getSchoolTeams(teams);
//
//        //取出每一所学校各个竞赛项目的人员数
//        ArrayList<Contestconfig> contestconfigs = new ArrayList<>();
//        contestconfigrepository.findAll().forEach(contestconfig -> {
//            contestconfigs.add(contestconfig);
//        });
//
//        schoolTeams.forEach(schoolTeam -> {
//            schoolTeam.setMembers(0);
//
//            contestconfigs.forEach(contestconfig -> {
//                //计算学校每一场的总人数
//                Contestid contestid = new Contestid();
//                contestid.setMembers(0);
//                contestid.setContestid(contestconfig.getId());
//
//                //处理每一场的单一竞赛项目人数
//                contestconfig.getContestgroup().forEach(item -> {
//                    int members = teamrepository.countByContestitemContainingAndSchoolname(item, schoolTeam.getSchoolname());
//                    int presentationMembers = teamrepository.countByMembernameNotNullAndContestitemContainingAndSchoolname(item, schoolTeam.getSchoolname());
//
////                    简报可能两人一组
//                    if (presentationMembers != 0) {
////                        System.out.println(String.format("%s,%s:%s", schoolTeam.getSchoolname(), item, presentationMembers));
//
//                        members = members + presentationMembers;
//                    }
//                    ContestItem contestitem = new ContestItem();
//                    contestitem.setMembers(members);
//                    contestitem.setItem(item);
//                    contestitem.setContestid(contestconfig.getId());
//                    schoolTeam.getContestitems().add(contestitem);
//
//                    contestid.setMembers(contestid.getMembers() + members);
//                });
//
//
//                schoolTeam.getContestids().add(contestid);
//                schoolTeam.setMembers(schoolTeam.getMembers() + contestid.getMembers());
//
//            });
//            schoolTeamRepository.save(schoolTeam);
//
//        });


//        teams = teamrepository.findAllByOrderBySchoolname();
//        teams.forEach(team -> {
//            Pattern pattern = Pattern.compile("(.{1,3}區)");
//            Matcher matcher = pattern.matcher(team.getSchoolname());
//            if (matcher.find()) {
//                logger.info(matcher.group(0));
//            }
//
//        });


//        Googlemap googlemap = new Googlemap();
//        googlemap.setId("12345678");
//        googlemap.setStartid("1234");
//        googlemap.setStartname("起点");
//        googlemap.setEndid("5678");
//        googlemap.setEndname("终点");
//        googlemap.setDistance(999.99);
//        googlemapRepository.save(googlemap);

        //update
//        Optional<Googlemap> googlemap = googlemapRepository.findById("12345678");
//        googlemap.get().setDistance(9999999.12345678);
//        googlemapRepository.save(googlemap.get());


        System.out.println("系统启动成功");
    }


//    List<SchoolTeam> getSchoolTeams(List<Team> teams) {
//        System.out.println("teams size:"+ teams.size());
//        List<SchoolTeam> schoolTeams = new ArrayList<>();
//        teams.forEach(team -> {
//            Boolean isExist = schoolTeams.stream().anyMatch(schoolTeam ->
//                    schoolTeam.getSchoolname().equals(team.getSchoolname()));
//
//            if (!isExist) {
//                SchoolTeam schoolteam = new SchoolTeam();
//                School school = schoolrepository.findBySchoolname(team.getSchoolname());
//                schoolteam.setSchoolname(team.getSchoolname());
//                schoolteam.setSchoolid(school.getSchoolid());
//                schoolTeams.add(schoolteam);
//
//            }
//
//        });
//        return schoolTeams;
//    }
}

