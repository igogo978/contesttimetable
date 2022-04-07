package app.contestTimetable.api;


import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Inform;
import app.contestTimetable.model.pocketlist.LocationSummary;
import app.contestTimetable.model.pocketlist.Pocketlist;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.PocketlistRepository;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
    LocationRepository locationRepository;

    @Autowired
    InformService informService;


    @Autowired
    XlsxService xlsxService;

    @Autowired
    PdfService pdfService;

    @Autowired
    TeamService teamService;
    ObjectMapper mapper = new ObjectMapper();

    @PostMapping(value = "/api/pocketlist")
    public String updatePocketlist(@RequestBody String payload) throws IOException {

        pocketlistService.updatePocketlist(payload);

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return dateTime.format(formatter);
    }

    @DeleteMapping(value = "/api/pocketlist")
    public String deletePacketlist(@RequestBody String payload) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        pocketlistService.delete();

        return dateTime.format(formatter);
    }

    @GetMapping(value = "/api/pocketlist")
    public Map<String, List<LocationSummary>> getLocationSummary() throws IOException {

        Map<String, List<LocationSummary>> locationMp = new HashMap<>();
        locationMp = pocketlistService.getSummary();

        return locationMp;
    }

    @GetMapping(value = "/api/pocketlist/download")
    public ResponseEntity<Resource> getPocketListDownload() throws IOException {


        List<Pocketlist> lists = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(team->lists.add(team));
        pocketlistRepository.findAll().forEach(lists::add);

        String filename = "pocketlist.json";
        //直接輸出
        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resourceStream, lists);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s", filename));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping(value = "/api/pocketlist/player")
    public List<Team> getPocketListByPlayer() throws IOException {
        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByPlayer();

        return teams;
    }

    @GetMapping(value = "/api/pocketlist/player/download")
    public ResponseEntity<Resource> downloadPocketlistReport() throws IOException {
        logger.info("download pocketlist by player");
        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByPlayer();


        String filename = "report-by-player";
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
        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
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

        String filename = "report-by-location";
        //直接輸出
        XSSFWorkbook wb = xlsxService.createPocketlistByLocation(teams);

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

    @GetMapping(value = "/api/pocketlist/inform/location/download", produces = "application/zip")
    public void doInformLocation(HttpServletRequest request, HttpServletResponse response) throws IOException {

        File baseDir = new File("/tmp/contest");
        baseDir.mkdir();
        File informDir = new File("/tmp/contest/inform");
        if (informDir.exists()) {
            FileUtils.forceDelete(informDir);
        }
        informDir.mkdir();

        String filename = "inform-location.zip";
        List<Inform> informs = new ArrayList<>();
        informs = informService.getInformsByLocation(Boolean.FALSE);

        List<File> xlsxList = new ArrayList<>();

        //write xlsx files
        informs.forEach(inform -> {
            try {
                File xlsx = new File(informDir + "/" + inform.getLocation() + "-" + inform.getContestItem() + ".xlsx");
                xlsxList.add(xlsx);

                FileOutputStream out = new FileOutputStream(xlsx);

                XSSFWorkbook wb = xlsxService.createPocketlistInformLocation(inform.getTeams());
                wb.write(out);


            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        logger.info("xlsx files have been created, start to zip");
        //zip xlsx files and provide download
        //setting headers
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=" + filename);

        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

        xlsxList.forEach(xlsx -> {
            try {
                zipOutputStream.putNextEntry(new ZipEntry(xlsx.getName()));
                FileInputStream fileInputStream = new FileInputStream(xlsx);

                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
        logger.info("inform location zip file have been created!");
        zipOutputStream.close();

    }

    @PostMapping(value = "/api/pocketlist/inform/all/download")
    public ResponseEntity<Resource> doInformAll(HttpServletRequest request, @RequestBody String payload) throws IOException {

        logger.info(mapper.writeValueAsString(payload));
        JsonNode root = mapper.readTree(payload);
        Boolean isVisiblePasswd = root.get("passwd").asText().equals("23952340");
        logger.info(String.valueOf(isVisiblePasswd));

        List<Inform> informs = new ArrayList<>();
        informs = informService.getInformsAll(isVisiblePasswd);

        String filename = "inform-all.pdf";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = pdfService.doInformCoverAndTeamPDF(informs);
        logger.info("pdf file has been created ");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s", filename));
        headers.setContentType(MediaType.APPLICATION_PDF);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);
    }


}




