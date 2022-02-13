package app.contestTimetable;

import app.contestTimetable.service.InformCommentsService;
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

@SpringBootApplication
public class ContestTimetableApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(ContestTimetableApplication.class);
    @Autowired
    InformCommentsService informCommentsService;
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

        logger.info("系统启动成功");
    }

}

