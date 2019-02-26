package app.contestTimetable;

import app.contestTimetable.model.*;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.SchoolRepository;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.XlsxService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;

@SpringBootApplication
public class ContestTimetableApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TeamRepository teamrepository;


    @Autowired
    SchoolRepository schoolrepository;

    @Autowired
    LocationRepository locationrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;


    @Autowired
    XlsxService readxlsx;

    public static void main(String[] args) {
        SpringApplication.run(ContestTimetableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String cwd = System.getProperty("user.dir");
        String docPath = String.format("%s/docs", cwd);
        String settingPath = String.format("%s/settings", cwd);

        //读取参赛队伍
        ArrayList<Team> teams = new ArrayList<>();
        teams = readxlsx.getTeams(docPath);

        //empty all
        teamrepository.deleteAll();

        teams.forEach(team -> {
            teamrepository.save(team);
        });


        //读取台中市学校名单
        String tcschool = String.format("%s/%s", settingPath, "tcschool.xlsx");

        ArrayList<School> schools = new ArrayList<>();
        schools = readxlsx.getSchools(tcschool);

        ArrayList<School> tcschools = new ArrayList<>();


        schools.forEach(school -> {
            schoolrepository.save(school);
        });


        //读取竞赛设定档
        String contestconfigfile = String.format("%s/%s", settingPath, "contestconfig.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        if (new File(contestconfigfile).isFile()) {
            //create ObjectMapper instance
            System.out.println("读取设定档");
            root = mapper.readTree(new File(contestconfigfile));
            Contestconfig contestconfig = new Contestconfig();
            contestconfig.setCalculatejob(root.get("calculatejob").asInt());

            JsonNode node = root.get("setting");


            for (JsonNode subnode : node) {

                Integer id = subnode.get("id").asInt();

                String description = subnode.get("description").asText();
                contestconfig.setId(id);
                contestconfig.setDescription(description);
                subnode.get("contestgroup").forEach(element -> {
                    contestconfig.getContestgroup().add(element.asText());
                });

                contestconfigrepository.save(contestconfig);
            }
        }

        //读取场地
        String locationfile = String.format("%s/%s", settingPath, "location.xlsx");
        ArrayList<Location> locations = new ArrayList<>();
        locations = readxlsx.getLocations(locationfile);

        locations.forEach(location -> {
            School school = schoolrepository.findBySchoolname(location.getLocationname());
            location.setSchoolid(school.getSchoolid());
        });

        //存入location
        locations.forEach(location -> {
            locationrepository.save(location);
        });


//        SchoolTeam team = new SchoolTeam();
//        team.setMembers(1);
//        team.setSchoolid("123456");
        ArrayList<SchoolTeam> schoolteams = new ArrayList<>();
//        schoolteams.add(team);

//         SchoolTeam schoolteam = schoolteams.stream().filter(schoolTeam -> schoolTeam.getSchoolid().equals("123456"))
//         .findFirst().get();

//         Boolean isExist = schoolteams.stream().anyMatch(schoolTeam -> schoolTeam.getSchoolid().equals("1234567"));
//        System.out.println(isExist);

        System.out.println("服务成功启动");
//        //find contest group
//        System.out.println("find out contestgroup...");
//
//        Contestconfig contestgroup = contestconfigrepository.findById(1).orElse(null);
//        contestgroup.getContestgroup().forEach(g -> System.out.println(g));

////        System.out.println(teamrepository.findByContestgroupContainingAndContestgroupContaining("SCRATCH".toUpperCase(),"國中").size());
//        teamrepository.findByContestgroupContainingAndContestgroupContaining("SCRATCH".toUpperCase(),"國中").forEach(t->{
//            System.out.println(t.getContestgroup());
//        });


    }
}

