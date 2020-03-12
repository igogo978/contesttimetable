package app.contestTimetable.api;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Informcover;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.*;
import app.contestTimetable.service.PocketlistService;
import app.contestTimetable.service.XlsxService;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PocketlistApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContestconfigRepository contestconfigRepository;

    @Autowired
    PocketlistService pocketlistService;

    @Autowired
    PocketlistRepository pocketlistRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

//    @Autowired
//    LocationRepository locationRepository;

    @Autowired
    XlsxService xlsxService;


    @PostMapping(value = "/api/pocketlist")
    public String postReport(@RequestBody String payload) throws IOException {

        pocketlistService.updatePocketlist(payload);

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        return dateTime.format(formatter);
    }

    @GetMapping(value = "/api/pocketlist")
    public List<Pocketlist> getReport() throws IOException {

        List<Pocketlist> lists = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(team->lists.add(team));
        pocketlistRepository.findAll().forEach(lists::add);

        return lists;
    }


    @GetMapping(value = "/api/pocketlist/player")
    public List<Team> getPocketListByPlayer() throws IOException {
        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByPlayer();

        return teams;
    }


    @GetMapping(value = "/api/pocketlist/player/download")
    public ResponseEntity<Resource> downloadPocketlistReport() throws IOException {
        logger.info("download pocketlist by player");
        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByPlayer();


        String filename = "report-by-player";
        //直接輸出
        XSSFWorkbook wb = xlsxService.createPocketlistByPlayer(teams);

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


    @GetMapping(value = "/api/pocket/location")
    public List<Team> getPocketListByLocation() throws IOException {
        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByLocation();

        return teams;
    }


    @GetMapping(value = "/api/pocketlist/location/download")
    public ResponseEntity<Resource> downloadPocketlistReportByLocation() throws IOException {

        List<Team> teams = new ArrayList<>();
        teams = pocketlistService.getPocketlistByLocation();

        String filename = "report-by-location";
        //直接輸出
        XSSFWorkbook wb = xlsxService.createPocketlistByLocation(teams);

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


    @GetMapping(value = "/api/pocketlist/inform/cover/download")
    public ResponseEntity<Resource> downloadPocketlistReportInformTeacher() throws IOException {
        String filename = "inform-teacher";
        logger.info("create pdf");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc);
        // Create a PdfFont
        PdfFont font = PdfFontFactory.createFont("/home/igogo/font/TW-Kai-98_1.ttf", PdfEncodings.IDENTITY_H, true);

//        contestconfigRepository.findAll().forEach(contestconfig -> logger.info(String.valueOf(contestconfig.getId())+contestconfig.getDescription()));
        List<Contestconfig> configs = contestconfigRepository.findAllByOrderByIdAsc();


        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        locations.removeIf(location -> location.getLocationname().equals("未排入"));

//        locations.forEach(location -> logger.info(location.getLocationname()));
        List<Informcover> informs = new ArrayList<>();
        configs.forEach(config -> {
//            logger.info(String.format("%s,%s,%s", config.getId(), config.getContestgroup(), config.getDescription()));


            //4 个场次
            config.getContestgroup().forEach(contestitem -> {
//                List<Team> teams = teamRepository.findByContestitemContainingOrderByLocationDesc(contestitem.toUpperCase());

                locations.forEach(location -> {
                    Informcover inform = new Informcover();

                    inform.setLocation(location.getLocationname());
                    inform.setContestItem(contestitem.toUpperCase());
                    List<Team> teams = teamRepository.findByLocationAndContestitemContainingOrderByLocationDesc(location.getLocationname(), contestitem.toUpperCase());
                    inform.setTeams(teams.size());
//                    teams.forEach(team -> {
//                        logger.info(String.format("%s,%s at %s", contestitem, team.getContestitem(), team.getLocation()));
//
//                    });

                    logger.info(String.format("%s,%s,%s", inform.getLocation(), inform.getContestItem(), inform.getTeams()));
                });


            });

        });


        for (int i = 0; i < 2; i++) {

            if (i != 0) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            Paragraph head = new Paragraph();
            head.add("臺中市108年度中小學資訊網路應用競賽決賽選手帳號密碼").setFont(font).setBold().setFontSize(40).setTextAlignment(TextAlignment.CENTER);


            document.add(head);

            Paragraph blank = new Paragraph("\n");
            document.add(blank);


            Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

            Paragraph paragraph = new Paragraph("試場學校：西區大同國民小學").setFont(font);
            paragraph.add("\n");
            paragraph.add("競賽時間：3/12(二)上午9:00-12:00");
            Cell cell = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font)
                    .setFontSize(28)
                    .add(blank)
                    .add(paragraph)
                    .add(blank);
            table.addCell(cell);

            paragraph = new Paragraph("項目類別").setFont(font);
            paragraph.add("\n");
            paragraph.add("SCRATCH應用競賽(全部)");


            cell = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font)
                    .setFontSize(28)
                    .add(blank)
                    .add(paragraph)
                    .add(blank);
            table.addCell(cell);


            paragraph = new Paragraph("決賽選手數量：26人").setFont(font);
            paragraph.add("\n");
            paragraph.add("帳號密碼通知單數量：26張");


            cell = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font)
                    .setFontSize(20)
                    .add(blank)
                    .add(paragraph)
                    .add(blank);
            table.addCell(cell);


            document.add(table);

        }


        document.close();
        logger.info("pdf file has been created ");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s", "inform.pdf"));
        headers.setContentType(MediaType.APPLICATION_PDF);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);
    }


}
