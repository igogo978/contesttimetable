package app.contestTimetable.api;


import app.contestTimetable.model.PocketPlayer;
import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.Team;
import app.contestTimetable.repository.PocketlistRepository;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.PocketlistService;
import app.contestTimetable.service.XlsxService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PocketlistApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    PocketlistService pocketlistService;

    @Autowired
    PocketlistRepository pocketlistRepository;


    @Autowired
    TeamRepository teamRepository;

    @Autowired
    XlsxService xlsxService;

    @PostMapping(value = "/api/pocketlist")
    public String postReport(@RequestBody String payload) throws IOException {
        pocketlistService.updatePocketlist(payload);

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


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
//        List<Pocketlist> pocketlist = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(pocketlist::add);
        final List<Team> teams = new ArrayList<>();
        teamRepository.findAllByOrderBySchoolname().forEach(teams::add);

        return teams;
    }


    @GetMapping(value = "/api/pocketlist/player/download")
    public ResponseEntity<Resource> downloadPocketlistReport() throws IOException {

        final List<Team> teams = new ArrayList<>();
        teamRepository.findAllByOrderBySchoolname().forEach(teams::add);



        String filename = "report";
        //直接輸出
        XSSFWorkbook wb = xlsxService.createPocketlist(teams);

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
//        List<Pocketlist> pocketlist = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(pocketlist::add);
        final List<Team> teams = new ArrayList<>();
        teamRepository.findAllByOrderByLocation().forEach(teams::add);

        return teams;
    }


}
