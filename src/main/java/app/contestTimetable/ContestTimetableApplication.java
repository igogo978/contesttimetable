package app.contestTimetable;

import app.contestTimetable.model.report.Report;
import app.contestTimetable.model.report.ReportBody;
import app.contestTimetable.repository.*;
import app.contestTimetable.service.XlsxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.UUID;

@SpringBootApplication
public class ContestTimetableApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(ContestTimetableApplication.class);
    @Autowired
    TeamRepository teamrepository;
    @Autowired
    SchoolRepository schoolrepository;
    @Autowired
    LocationRepository locationrepository;
    @Autowired
    ContestconfigRepository contestconfigrepository;
    @Autowired
    SchoolTeamRepository schoolTeamRepository;
    @Autowired
    XlsxService readxlsx;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    ReportBodyRepository reportBodyRepository;


    @Value("${multipart.location}")
    private Path path;

    public static void main(String[] args) {
        SpringApplication.run(ContestTimetableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Files.isDirectory(path)) {
            Files.createDirectory(path);
            Files.createDirectory(Path.of("/tmp/poifiles"));
            logger.info("create contest tmp directory " + path.toString());
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
            Files.setPosixFilePermissions(Path.of("/tmp/poifiles"), PosixFilePermissions.fromString("rwxrwxrwx"));
        }


//        ReportBody reportBody = new ReportBody();
////        reportBody = reportBodyRepository.findByUuid("aaa").get();
//
//        Report report = reportBody.getReport();
//        report.setReport("cc");
//        report.setScores(8);
//
//        reportBody.setReport(report);
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(mapper.writeValueAsString(reportBody));
//
//        reportBodyRepository.save(reportBody);
//        reportRepository.save(report);

//        System.out.println(reportBody);

        System.out.println("系统启动成功");
    }

}

