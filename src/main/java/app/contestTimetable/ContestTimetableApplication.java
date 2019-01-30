package app.contestTimetable;

import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.School;
import app.contestTimetable.model.Team;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.SchoolRepository;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.ReadXlsxService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class ContestTimetableApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TeamRepository teamrepository;


    @Autowired
    SchoolRepository schoolrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;


    @Autowired
    ReadXlsxService readxlsx;

    public static void main(String[] args) {
        SpringApplication.run(ContestTimetableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String cwd = System.getProperty("user.dir");
        String docPath = String.format("%s/docs", cwd);

        //读取参赛队伍
        ArrayList<Team> teams = new ArrayList<>();
        teams = readxlsx.getTeams(docPath);

        //empty all
        teamrepository.deleteAll();

        teams.forEach(team -> {
            teamrepository.save(team);
        });


        //读取台中市学校名单
        String settingPath = String.format("%s/settings", cwd);
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

        //確認設定檔
        if (new File(contestconfigfile).isFile()) {
            //create ObjectMapper instance
            System.out.println("读取设定档");
            root = mapper.readTree(new File(contestconfigfile));

            JsonNode node = root.get("setting");


            for (JsonNode subnode : node) {
                Contestconfig contestconfig = new Contestconfig();
                Integer priority = subnode.get("priority").asInt();

                String description = subnode.get("description").asText();
                contestconfig.setPriority(priority);
                contestconfig.setDescription(description);
                subnode.get("contestgroup").forEach(element -> {
                    contestconfig.getContestgroup().add(element.asText());
                });

                contestconfigrepository.save(contestconfig);
            }
        }


    }
}

