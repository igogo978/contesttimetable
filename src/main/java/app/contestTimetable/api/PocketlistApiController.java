package app.contestTimetable.api;


import app.contestTimetable.model.Report;
import app.contestTimetable.model.ReportScoresSummary;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Inform;
import app.contestTimetable.model.pocketlist.LocationSum;
import app.contestTimetable.model.pocketlist.Pocketlist;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.*;
import app.contestTimetable.service.*;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    ReportRepository reportRepository;

    @Autowired
    ReportScoresSummaryRepository reportScoresSummaryRepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

    @Autowired
    InformService informService;

//    @Autowired
//    LocationRepository locationRepository;

    @Autowired
    XlsxService xlsxService;

    @Autowired
    PdfService pdfService;

    @Autowired
    TeamService teamService;


    //    String twFont = "/opt/font/TW-Kai-98_1.ttf";
//    String contestHeader = "臺中市110年度中小學資訊網路應用競賽決賽";

    @PostMapping(value = "/api/pocketlist")
    public String postReport(@RequestBody String payload) throws IOException {

        Report report = new Report();
        report.setUuid("1");
        report.setReport(payload);
        report.setScores(1.0);
        reportRepository.save(report);

        ReportScoresSummary reportScoresSummary = new ReportScoresSummary();
        reportScoresSummary.setUuid("1");
        reportScoresSummary.setScores(1.0);
        reportScoresSummaryRepository.save(reportScoresSummary);

        pocketlistService.updatePocketlist(payload);

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        return dateTime.format(formatter);
    }

    @DeleteMapping(value = "/api/pocketlist")
    public String deletePacketlist(@RequestBody String payload) {
        logger.info(payload);
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        pocketlistRepository.deleteAll();
        teamRepository.findAllByOrderByLocation().forEach(team -> {
            team.setLocation("");
            teamRepository.save(team);
        });

        return dateTime.format(formatter);


    }


    @GetMapping(value = "/api/pocketlist")
    public Map<String, List<LocationSum>> getLocationSummary() throws IOException {

//        String str = "打字".toUpperCase(); //for usb disk cacluating
        String str = "-----".toUpperCase();

        String excludeItem = String.format("(.*)%s(.*)", str);

        Map<String, List<LocationSum>> locationMp = new HashMap<>();


        List<Location> locations = locationRepository.findBySchoolidNotIn(Arrays.asList("999999"));


        locations.forEach(location -> {
            List<LocationSum> lists = new ArrayList<>();

            AtomicInteger contestid = new AtomicInteger(1);


            contestconfigRepository.findAllByOrderByIdAsc().forEach(contestconfig -> {
                LocationSum locationSum = new LocationSum();

                locationSum.setLocation(location.getLocationname());
                AtomicInteger contestidMembers = new AtomicInteger();
                contestidMembers.set(0);
                contestconfig.getContestgroup().forEach(contestitem -> {
                    List<Team> teams = new ArrayList<>();
                    AtomicInteger contestitemMembers = new AtomicInteger(0);

//                    if (!contestitem.matches(excludeItem)) {
                    teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationname(), contestitem);
                    teams.forEach(team -> {
                        contestitemMembers.updateAndGet(n -> n + team.getMembers());
                    });
//                        logger.info(String.format("%s, %s,%s", location.getLocationname(), contestitem, contestidMembers.get()));
                    locationSum.getContestitem().put(contestitem, contestitemMembers.get());

//                    }
                    contestidMembers.updateAndGet(n -> n + contestitemMembers.get());

                });
//                logger.info(String.valueOf(contestid.get()));
                locationSum.setContestid(contestid.get());
                locationSum.setMembers(contestidMembers.get());
                contestid.incrementAndGet();
                lists.add(locationSum);


            });

            List<LocationSum> items = new ArrayList<>();
            locationMp.computeIfAbsent(location.getLocationname(), k -> items);
            lists.forEach(list -> items.add(list));
//            lists.clear();

        });

        ObjectMapper mapper = new ObjectMapper();
        logger.info(mapper.writeValueAsString(locationMp));

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
            logger.info("delete anyway");
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

    @GetMapping(value = "/api/pocketlist/inform/all/download")
    public ResponseEntity<Resource> doInformAll(HttpServletRequest request) throws IOException {

        Boolean isLogin = request.getSession(false) == null ? Boolean.FALSE : Boolean.TRUE;
        List<Inform> informs = new ArrayList<>();
//        informs = pdfService.doInformAll(isLogin);
        informs = informService.getInformsAll(isLogin);

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




