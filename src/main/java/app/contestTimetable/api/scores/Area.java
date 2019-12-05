package app.contestTimetable.api.scores;


import app.contestTimetable.model.Areascore;
import app.contestTimetable.repository.AreascoreRepository;
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
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Area {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    AreascoreRepository areascoreRepository;

    @Autowired
    XlsxService createxlsx;

    @GetMapping(value = "/api/scores/area")
    public List<Areascore> getScoresByArea() throws IOException {

        List<Areascore> areas = new ArrayList<>();
        areascoreRepository.findAll().forEach(areas::add);
        return areas;
    }


    @GetMapping(value = "/api/scores/area/download")
    public ResponseEntity<Resource> downloadScoresByArea() throws IOException {

        List<Areascore> allareas = new ArrayList<>();
        areascoreRepository.findAll().forEach(allareas::add);

        List<Areascore> areas = new ArrayList<>();
        List<String> areasUuid = new ArrayList<>();

        allareas.forEach(area -> {
            String uuid1 = String.format("%s%s", area.getStartarea(), area.getEndarea());
            String uuid2 = String.format("%s%s", area.getEndarea(), area.getStartarea());
            if (!(areasUuid.contains(uuid1) || areasUuid.contains(uuid2))) {
                areasUuid.add(uuid1);
                areas.add(area);
            }

        });

        logger.info("area uuid size:" + areasUuid.size());

        String filename = "area";
        //直接輸出
        XSSFWorkbook wb = createxlsx.createAreascores(areas);

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
}


