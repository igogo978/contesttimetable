package app.contestTimetable.api;

import app.contestTimetable.model.report.Report;
import app.contestTimetable.model.report.ReportBody;
import app.contestTimetable.model.report.ReportFull;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.ReportService;
import app.contestTimetable.service.TicketService;
import app.contestTimetable.service.XlsxService;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
public class ReportApiController {

    Logger logger = LoggerFactory.getLogger(ReportApiController.class);

    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    ReportService reportService;

    @Autowired
    TicketService ticketservice;

    @Autowired
    XlsxService createxlsx;


//    @GetMapping(value = "/api/report/{page}")
//    public List<Report> getReportsByPage(@PathVariable Optional<Integer> page) {
//        List<Report> reports = new ArrayList<>();
////        reportRepository.findAll().forEach(reports::add);
//
//        if (!page.isPresent()) {
//            page.orElse(1);
//        }
//
//        return reportRepository.findAllByOrderByScoresAsc().subList(page.get(), page.get() + 300);
//
//    }


    @GetMapping(value = "/api/report/size")
    public Integer getReportSize() {
        return reportService.getReportBodies().size();
    }

    @GetMapping(value = "/api/report/scores/1")
    public Report get1stReport() {
        return reportService.get1stReport();
    }

    @GetMapping(value = "/api/report/download")
    public ResponseEntity<Resource> downloadReport() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        List<ReportFull> reportFulls = reportService.getReportFulls();
        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
        resourceStream.write(mapper.writeValueAsBytes(reportFulls));

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

    @GetMapping(value = "/api/report/v2")
    public List<Report> getReportsV2() {
        return reportService.getReports();
    }

    @GetMapping(value = "/api/reportbody/{uuid}")
    public ReportBody getReportBody(@PathVariable("uuid") String uuid) {
        return reportService.getReportbody(uuid);

    }


    @GetMapping(value = "/api/report/uuid/{uuid}")
    public Report getReport(@PathVariable("uuid") String uuid) {
        Optional<Report> report = reportService.getReport(uuid);
        if (report.isPresent()) {
            return report.get();
        }
        return new Report();

    }


    @PostMapping(value = "/api/report/uuid/{uuid}")
    public String updateReport(@RequestBody String payload) throws IOException {

        reportService.updateReport(payload);
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return formatter.toString();
    }


    @GetMapping(value = "/api/report/download/{uuid}")
    public ResponseEntity<Resource> downloadReport(@PathVariable("uuid") String uuid) throws IOException {
//        public ArrayList<String> downloadReport (@PathVariable("uuid") String uuid) throws IOException {
//        logger.info("download selected report");


//        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(report.getReport()));

        List<String> teams = reportService.getReport(reportService.getReportbody(uuid));
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


    @DeleteMapping(value = "/api/report")
    public void deleteReports() {
        logger.info("delete reports");
//        reportRepository.deleteAll();
        reportService.delete();
    }


}
