package app.contestTimetable.api;

import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.Team;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.SelectedReportService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SelectedReportApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    SelectedReportService selectedreportservice;

    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    XlsxService createxlsx;

    @Autowired
    TeamRepository teamrepository;


//    @GetMapping(value = "/report/selected/byteam/{contestid}")
//    public ArrayList<Team> getSelectedReportsByTeam(@PathVariable("contestid") int contestid) throws IOException {
//
//
//        return selectedreportservice.selectedReportByTeam(contestid);
//
//    }
//
//    @GetMapping(value = "/report/selected/bylocation/{contestid}")
//    public ArrayList<Team> getSelectedReportsByLocation(@PathVariable("contestid") int contestid) throws IOException {
//
//        return selectedreportservice.selectedReportByLocation(contestid);
//
//    }


//    @GetMapping(value = "/report/selected/byteam")
//    public ResponseEntity<Resource> downloadAllReportByteam() throws IOException {
//
//
//        List<Team> teams = teamrepository.findAllByOrderBySchoolname();
//        String filename = "byschool-All";
//        //直接輸出
//        XSSFWorkbook wb = createxlsx.createSelectedReport(teams);
//
//        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
//        wb.write(resourceStream);
//        wb.close();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add("charset", "utf-8");
//        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
//        return ResponseEntity.ok().headers(headers).body(resource);
//    }


//    @GetMapping(value = "/report/selected/bylocation")
//    public ResponseEntity<Resource> downloadAllReportBylocation() throws IOException {
//
//
//        List<Team> teams = teamrepository.findAllByOrderByLocation();
//        String filename = "bylocation-All";
//
//        //save contesttime in team.description
//
//
//        //直接輸出
//        XSSFWorkbook wb = createxlsx.createPocketlistByLocation(teams);
//
//        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
//        wb.write(resourceStream);
//        wb.close();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add("charset", "utf-8");
//        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
//        return ResponseEntity.ok().headers(headers).body(resource);
//    }


//    @GetMapping(value = "/report/selected/byteam/{contestid}/excel")
//    public ResponseEntity<Resource> downloadReportByteam(@PathVariable("contestid") Integer contestid) throws IOException {
//
//
//        List<Team> teams = new ArrayList<>();
//
//
//        //find contestgroup
//        Contestconfig contestconfig = contestconfigrepository.findById(contestid).get();
//        contestconfig.getContestgroup().forEach(contestgroup -> {
//            teamrepository.findByContestitemContainingOrderBySchoolnameDesc(contestgroup).forEach(team -> {
//                teams.add(team);
//            });
//        });
//
//
////        ObjectMapper mapper = new ObjectMapper();
////        logger.info(mapper.writeValueAsString(report.getReport()));
//
//        String filename = "byschool場次" + contestid;
//        //直接輸出
//        XSSFWorkbook wb = createxlsx.createSelectedReport(teams);
//
//        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
//        wb.write(resourceStream);
//        wb.close();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add("charset", "utf-8");
//        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
//        return ResponseEntity.ok().headers(headers).body(resource);
//
//    }
//
//
//    @GetMapping(value = "/report/selected/bylocation/{contestid}/excel")
//    public ResponseEntity<Resource> downloadReportByLocation(@PathVariable("contestid") Integer contestid) throws IOException {
//
//        ArrayList<Team> teams = new ArrayList<>();
//        //find contestgroup
//        Contestconfig contestconfig = contestconfigrepository.findById(contestid).get();
//        contestconfig.getContestgroup().forEach(contestgroup -> {
//            teamrepository.findByContestitemContainingOrderByLocationDesc(contestgroup).forEach(team -> {
//                teams.add(team);
//            });
//        });
//
//
//        String filename = "bylocation場次" + contestid;
//        ;
//        //直接輸出
//        XSSFWorkbook wb = createxlsx.createSelectedReport(teams);
//
//        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
//        wb.write(resourceStream);
//        wb.close();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add("charset", "utf-8");
//        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
//        return ResponseEntity.ok().headers(headers).body(resource);
//
//    }
}
