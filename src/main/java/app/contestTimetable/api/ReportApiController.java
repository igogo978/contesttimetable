package app.contestTimetable.api;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.Selectedreport;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.repository.SelectedreportRepository;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.XlsxService;
import app.contestTimetable.service.ReportService;
import app.contestTimetable.service.TicketService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@RestController
public class ReportApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ReportRepository reportrepository;

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


    @GetMapping(value = "/api/report/{contestid}")
    public ArrayList<Report> getReports(@PathVariable("contestid") int contestid) {
        ArrayList<Report> reports = new ArrayList<>();
        reports = reportrepository.findTop10ByContestidOrderByDistanceAsc(contestid);


        return reports;

    }

    @GetMapping(value = "/api/report/uuid/{uuid}")
    public Report getReport(@PathVariable("uuid") String uuid) {

        return reportrepository.findByUuid(uuid);

    }


    @GetMapping(value = "/api/report/download/{uuid}")
    public ResponseEntity<Resource> downloadReport(@PathVariable("uuid") String uuid) throws IOException {
//        public ArrayList<String> downloadReport (@PathVariable("uuid") String uuid) throws IOException {
//        logger.info("download selected report");


        Report report = reportrepository.findByUuid(uuid);

//        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(report.getReport()));

        ArrayList<String> teams = reportservice.getReport(report);
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
//        return reportservice.getReport(report);


    }


    @GetMapping(value = "/report/{contestid}/lock/{uuid}")
    public String lockReportInContestid(@PathVariable("contestid") int contestid,
                                        @PathVariable("uuid") String uuid) throws IOException {
        String response = null;
        Report report = new Report();
        if (reportservice.isExistUuid(uuid)) {
            report = reportrepository.findByUuid(uuid);
//            reportservice.updateTicket(report);
            ticketrepository.deleteAll();
//            response = reportservice.updateTicket(report);
            response = report.getReport();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            //update ticket
            ticketservice.updateTicket(report);


            //update selected report
            if (selectedreportrepository.countByContestid(contestid) == 0) {
                Selectedreport selectedreport = new Selectedreport();
                selectedreport.setContestid(contestid);
                selectedreport.setReport(report.getReport());
                selectedreport.setDistance(report.getDistance());

                selectedreportrepository.save(selectedreport);
            }

            //update team's location
            logger.info("update team location");
            reportservice.updateTeamLocation(report);


        } else {
            response = "no uuid records";
        }


        return response;

    }
}
