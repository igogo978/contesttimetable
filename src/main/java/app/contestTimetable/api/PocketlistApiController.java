package app.contestTimetable.api;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.*;
import app.contestTimetable.service.PocketlistService;
import app.contestTimetable.service.XlsxService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PocketlistApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContestconfigRepository contestconfigRepository;

    @Autowired
    PocketlistService pocketlistService;

    @Autowired
    PocketlistRepository pocketlistRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

//    @Autowired
//    LocationRepository locationRepository;

    @Autowired
    XlsxService xlsxService;



    @PostMapping(value = "/api/pocketlist")
    public String postReport(@RequestBody String payload) throws IOException {
        pocketlistService.updatePocketlist(payload);

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        return dateTime.format(formatter);
    }

    @GetMapping(value = "/api/pocketlist")
    public List<Pocketlist> getReport() throws IOException {

        List<Pocketlist> lists = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(team->lists.add(team));
        pocketlistRepository.findAll().forEach(lists::add);

        return lists;
    }


    @GetMapping(value = "/api/pocketlist/player")
    public List<Team> getPocketListByPlayer() throws IOException {
        final List<Team> teams = new ArrayList<>();


        //order by schoolname, then order by contestitem
        List<Contestconfig> contestconfigs = contestconfigRepository.findAllByOrderByIdAsc();
        List<SchoolTeam> schoolTeams = schoolTeamRepository.findAllByOrderByMembersDesc();

        schoolTeams.forEach(schoolTeam -> {
            contestconfigs.forEach(contestconfig -> {
                contestconfig.getContestgroup().forEach(contestitem -> {
                            logger.info(String.format("%s,%s", schoolTeam.getSchoolname(), contestitem));
                            teamRepository.findBySchoolnameAndContestitemContaining(schoolTeam.getSchoolname(), contestitem).forEach(team -> {
                                teams.add(team);
                            });
                        }

                );


            });
        });


        return teams;
    }


    @GetMapping(value = "/api/pocketlist/player/download")
    public ResponseEntity<Resource> downloadPocketlistReport() throws IOException {
        logger.info("download pocketlist by player");
        final List<Team> teams = new ArrayList<>();


        List<Contestconfig> contestconfigs = contestconfigRepository.findAllByOrderByIdAsc();
        List<SchoolTeam> schoolTeams = schoolTeamRepository.findAllByOrderByMembersDesc();

        schoolTeams.forEach(schoolTeam -> {
            contestconfigs.forEach(contestconfig -> {
                contestconfig.getContestgroup().forEach(contestitem -> {
                            teamRepository.findBySchoolnameAndContestitemContaining(schoolTeam.getSchoolname(), contestitem).forEach(team -> {
                                teams.add(team);
                            });
                        }

                );


            });
        });


        String filename = "report";
        //直接輸出
        XSSFWorkbook wb = xlsxService.createPocketlistByPlayer(teams);

        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
        wb.write(resourceStream);
        wb.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", "player"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);


    }


    @GetMapping(value = "/api/pocket/location")
    public List<Team> getPocketListByLocation() throws IOException {
        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByLocation();



        return teams;
    }


    @GetMapping(value = "/api/pocketlist/location/download")
    public ResponseEntity<Resource> downloadPocketlistReportByLocation() throws IOException {

        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByLocation();

        String filename = "report";
        //直接輸出
        XSSFWorkbook wb = xlsxService.createSelectedReportByLocation(teams);

        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
        wb.write(resourceStream);
        wb.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", "player"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);


    }


}
