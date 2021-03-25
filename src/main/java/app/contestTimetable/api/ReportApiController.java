package app.contestTimetable.api;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.ReportScoresSummary;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.repository.ReportScoresSummaryRepository;
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
import org.springframework.data.domain.Sort;
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

    Logger logger = LoggerFactory.getLogger(ReportApiController.class);

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ReportScoresSummaryRepository reportScoresSummaryRepository;

    @Autowired
    TicketRepository ticketrepository;


    @Autowired
    ReportService reportservice;

    @Autowired
    TicketService ticketservice;

    @Autowired
    XlsxService createxlsx;


    @GetMapping(value = "/api/report/{page}")
    public List<Report> getReportsByPage(@PathVariable Optional<Integer> page) {
        List<Report> reports = new ArrayList<>();
//        reportRepository.findAll().forEach(reports::add);

        if (!page.isPresent()) {
            page.orElse(1);
        }

        return reportRepository.findAllByOrderByScoresAsc().subList(page.get(),page.get()+300);

    }



    @GetMapping(value = "/api/report/size")
    public Integer getReportSize() {
        return reportRepository.findAllByOrderByScoresAsc().size();
    }

    @GetMapping(value = "/api/report")
    public List<ReportScoresSummary> getReports() {
        logger.info("reports size: "+ reportScoresSummaryRepository.findAllByOrderByScoresAsc().size());
        return reportScoresSummaryRepository.findAllByOrderByScoresAsc();

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


        ReportScoresSummary reportScoresSummary = new ReportScoresSummary();
        reportScoresSummary.setUuid(node.get("uuid").asText());
        reportScoresSummary.setScoresfrequency(node.get("scoresFrequency").asText());
        reportScoresSummary.setScores(Double.valueOf(df.format(node.get("totalscores").asDouble())));


        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


        logger.info("update a new report: " + dateTime.format(formatter) + ", scores: "+ reportScoresSummary.getScores());

        reportRepository.save(report);
        reportScoresSummaryRepository.save(reportScoresSummary);

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


    @DeleteMapping(value = "/api/report")
    public void deleteReports() {
        logger.info("delete reports");
        reportRepository.deleteAll();
        reportScoresSummaryRepository.deleteAll();
    }


}
