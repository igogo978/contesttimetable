package app.contestTimetable;

import app.contestTimetable.model.Areascore;
import app.contestTimetable.model.PriorityArea;
import app.contestTimetable.repository.AreascoreRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.PriorityAreaRepository;
import app.contestTimetable.service.InformCommentsService;
import app.contestTimetable.service.ScoresService;
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
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ContestTimetableApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(ContestTimetableApplication.class);
    @Autowired
    InformCommentsService informCommentsService;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    PriorityAreaRepository priorityAreaRepository;

    @Autowired
    ScoresService scoresService;

    @Autowired
    AreascoreRepository areascoreRepository;

    @Value("${multipart.location}")
    private Path path;

    public static void main(String[] args) {
        SpringApplication.run(ContestTimetableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        if (!Files.isDirectory(path)) {
            Files.createDirectory(path);
            logger.info("create contest tmp directory " + path.toString());
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
        }
        informCommentsService.getInformComments();


        logger.info("服務成功啟動");
    }

}

