package app.contestTimetable;

import app.contestTimetable.model.Team;
import app.contestTimetable.repository.TeamRepository;
import app.contestTimetable.service.InformCommentsService;
import app.contestTimetable.service.PdfService;
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
    TeamRepository teamRepository;

    @Autowired
    PdfService pdfService;

    @Value("${multipart.location}")
    private Path path;

    public static void main(String[] args) {
        SpringApplication.run(ContestTimetableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        String username1 = "王辰\uDBAF\uDF34";
        String username2 = "王\uDBAF\uDF34辰";
        String username3 = "\uDBAF\uDF34辰";
        String username4 = "\uDBAF\uDF34辰王";
        List<String> extHanziList = new ArrayList<>();

//        ExtHanzi extHanzi = extHanziRepository.findByHanzi("\uDBAF\uDF34");
//        extHanzi.setFont("twKaiPlusFont");

//        extHanziRepository.findAll().forEach(extHanzi -> extHanziList.add(extHanzi.getHanzi()));
//
//
//
//        String username = username4;
//        String extHanzi = (pdfService.containsExtHanzi(extHanziList, username));
//
//        System.out.println(username);
//        System.out.println(extHanzi);
////        try to split
//        System.out.println("position: " + username.indexOf(extHanzi));
//
//        if (username.indexOf(extHanzi) == 0) {
//            String commanHanzi = username.replace(extHanzi, "");
//            System.out.println(username.length());
//            System.out.println("replace extHanzi:" +commanHanzi.length());
////            System.out.println(extHanzi + ":plus font," + commanHanzi + ":default font");
//        }
//
//        if (username.indexOf(extHanzi) == 1) {
//            if (username.split(extHanzi).length == 1) {
//                System.out.println(username.split(extHanzi)[0] + ":default font" + extHanzi + ":plus font,");
//            }
//            if (username.split(extHanzi).length == 2) {
//                System.out.println(username.split(extHanzi)[0] + ":default font, " + extHanzi + ":plus font," + username.split(extHanzi)[1] + ":plus font,");
//            }
//
//        }
//
//        if (username.indexOf(extHanzi) == 2) {
//            System.out.println(username.split(extHanzi)[0] + ":default font, " + extHanzi + ":plus font,");
//
//        }

        //not in 1st position


        if (!Files.isDirectory(path)) {
            Files.createDirectory(path);
            logger.info("create contest tmp directory " + path.toString());
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
        }
        informCommentsService.getInformComments();


        logger.info("服務成功啟動");
    }

}

