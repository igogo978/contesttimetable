package app.contestTimetable;

import app.contestTimetable.repository.*;
import app.contestTimetable.service.XlsxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

@SpringBootApplication
public class ContestTimetableApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(ContestTimetableApplication.class);

    @Value("${multipart.location}")
    private Path path;

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
    ReportScoresSummaryRepository reportScoresSummaryRepository;

    public static void main(String[] args) {
        SpringApplication.run(ContestTimetableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Files.isDirectory(path)) {
            Files.createDirectory(path);
            Files.createDirectory(Path.of("/tmp/poifiles"));
            logger.info("create contest tmp directory "+ path.toString());
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
            Files.setPosixFilePermissions(Path.of("/tmp/poifiles"), PosixFilePermissions.fromString("rwxrwxrwx"));
        }

        System.out.println("系统启动成功");
    }

}

