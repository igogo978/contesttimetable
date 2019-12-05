package app.contestTimetable.api;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.Selectedreport;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.repository.SelectedreportRepository;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.ReportService;
import app.contestTimetable.service.TicketService;
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
public class ReportApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ReportRepository reportRepository;

    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    SelectedreportRepository selectedreportrepository;

    @Autowired
    ReportService reportservice;

    @Autowired
    TicketService ticketservice;

    @Autowired
    XlsxService createxlsx;


    @GetMapping(value = "/api/report")
    public List<Report> getReports() {
        List<Report> reports = new ArrayList<>();
//        reportRepository.findAll().forEach(reports::add);

        return reportRepository.findAllByOrderByScoresAsc();

    }

    @GetMapping(value = "/api/report/download")
    public ResponseEntity<Resource> downloadReport() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<Report> reports = new ArrayList<>();

        reportRepository.findAll().forEach(reports::add);

        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
//        wb.write(resourceStream);
        resourceStream.write(mapper.writeValueAsBytes(reports));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s.json", "reports"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);

    }


    @GetMapping(value = "/api/report/uuid/{uuid}")
    public Report getReport(@PathVariable("uuid") String uuid) {
        Optional<Report> report = reportRepository.findByUuid(uuid);
        if (report.isPresent()) {
            return report.get();
        }
        return new Report();

    }


    @PostMapping(value = "/api/report/uuid/{uuid}")
    public String postReport(@RequestBody String payload) throws IOException {

        Report report = new Report();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(payload);
        report.setUuid(node.get("uuid").asText());

        DecimalFormat df = new DecimalFormat(".#");
        report.setScores(Double.valueOf(df.format(node.get("totalscores").asDouble())));
        report.setReport(mapper.writeValueAsString(node.get("candidateList")));
        report.setScoresfrequency(node.get("scoresFrequency").asText());
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


        logger.info("update a new report:" + dateTime.format(formatter));

        reportRepository.save(report);


        return payload;
    }


    @GetMapping(value = "/api/report/download/{uuid}")
    public ResponseEntity<Resource> downloadReport(@PathVariable("uuid") String uuid) throws IOException {
//        public ArrayList<String> downloadReport (@PathVariable("uuid") String uuid) throws IOException {
//        logger.info("download selected report");


        Optional<Report> report = reportRepository.findByUuid(uuid);

//        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(report.getReport()));

        List<String> teams = reportservice.getReport(report.get());
        String filename = "report";
        //直接輸出
        XSSFWorkbook wb = createxlsx.create(teams);

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


//    @GetMapping(value = "/report/{contestid}/lock/{uuid}")
//    public String lockReportInContestid(@PathVariable("contestid") int contestid,
//                                        @PathVariable("uuid") String uuid) throws IOException {
//        String response = null;
//        Report report = new Report();
//        if (reportservice.isExistUuid(uuid)) {
//            report = reportRepository.findByUuid(uuid).get();
////            reportservice.updateTicket(report);
//            ticketrepository.deleteAll();
////            response = reportservice.updateTicket(report);
//            response = report.getReport();
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(response);
//
//            //update ticket
//            ticketservice.updateTicket(report);
//
//
//            //update selected report
//            if (selectedreportrepository.countByContestid(contestid) == 0) {
//                Selectedreport selectedreport = new Selectedreport();
//                selectedreport.setContestid(contestid);
//                selectedreport.setReport(report.getReport());
//                selectedreport.setDistance(report.getScores());
//
//                selectedreportrepository.save(selectedreport);
//            }
//
//            //update team's location
//            logger.info("update team location");
//            reportservice.updateTeamLocation(report);
//
//
//        } else {
//            response = "no uuid records";
//        }
//
//
//        return response;
//
//    }


    @DeleteMapping(value = "/api/report")
    public void deleteReports() {
        logger.info("delete reports");
        reportRepository.deleteAll();
    }


}
