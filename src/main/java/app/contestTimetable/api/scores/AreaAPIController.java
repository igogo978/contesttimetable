package app.contestTimetable.api.scores;


import app.contestTimetable.model.Areascore;
import app.contestTimetable.repository.AreascoreRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.service.LocationService;
import app.contestTimetable.service.SchoolTeamService;
import app.contestTimetable.service.ScoresService;
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
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AreaAPIController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    AreascoreRepository areascoreRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    SchoolTeamService schoolTeamService;

    @Autowired
    ScoresService scoresService;

    @Autowired
    XlsxService createxlsx;

    @Autowired
    LocationService locationService;

    @GetMapping(value = "/api/scores/area/{areaname}")
    public List<Areascore> getOneAreascores(@PathVariable String areaname) throws IOException {
        return scoresService.getScoresByAreaname(areaname);
    }

    @GetMapping(value = "/api/scores/area")
    public List<Areascore> getScoresByArea() throws IOException {
        return scoresService.getAllScoresByArea();
    }

    @GetMapping(value = "/api/scores/area/download")
    public ResponseEntity<Resource> downloadScoresByArea() throws IOException {

        List<Areascore> areascores = new ArrayList<>();
        areascores = scoresService.getAllScoresByArea();
        String filename = "area";
        //直接輸出
        XSSFWorkbook wb = createxlsx.createAreascores(areascores);

        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
        wb.write(resourceStream);
        wb.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);

    }


    @PutMapping(value = "/api/scores/area")
    public String updateScoresByArea(@RequestBody List<Areascore> areascores) throws IOException {

        areascores.forEach(areascore -> {
            areascoreRepository.save(areascore);
        });

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        return dateTime.format(formatter);

    }


}


