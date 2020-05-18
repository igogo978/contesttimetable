package app.contestTimetable.api;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.ReportScoresSummary;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Inform;
import app.contestTimetable.model.pocketlist.LocationSum;
import app.contestTimetable.model.pocketlist.Pocketlist;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.*;
import app.contestTimetable.service.PdfService;
import app.contestTimetable.service.PocketlistService;
import app.contestTimetable.service.TeamService;
import app.contestTimetable.service.XlsxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


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
    ReportRepository reportRepository;

    @Autowired
    ReportScoresSummaryRepository reportScoresSummaryRepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

//    @Autowired
//    LocationRepository locationRepository;

    @Autowired
    XlsxService xlsxService;

    @Autowired
    PdfService pdfService;

    @Autowired
    TeamService teamService;


    //    String twFont = "/opt/font/TW-Kai-98_1.ttf";
    String contestHeader = "臺中市109年度中小學資訊網路應用競賽決賽";

    @PostMapping(value = "/api/pocketlist")
    public String postReport(@RequestBody String payload) throws IOException {

        Report report = new Report();
        report.setUuid("1");
        report.setReport(payload);
        report.setScores(1.0);
        reportRepository.save(report);

        ReportScoresSummary reportScoresSummary = new ReportScoresSummary();
        reportScoresSummary.setUuid("1");
        reportScoresSummary.setScores(1.0);
        reportScoresSummaryRepository.save(reportScoresSummary);

        pocketlistService.updatePocketlist(payload);

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        return dateTime.format(formatter);
    }

    @DeleteMapping(value = "/api/pocketlist")
    public String deletePacketlist(@RequestBody String payload) {
        logger.info(payload);
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        pocketlistRepository.deleteAll();
        teamRepository.findAllByOrderByLocation().forEach(team -> {
            team.setLocation("");
            teamRepository.save(team);
        });

        return dateTime.format(formatter);


    }


    @GetMapping(value = "/api/pocketlist")
    public Map<String, List<LocationSum>> getLocationSummary() throws IOException {

//        String str = "打字".toUpperCase();
        String str = "-----".toUpperCase();

        String excludeItem = String.format("(.*)%s(.*)", str);

        Map<String, List<LocationSum>> locationMp = new HashMap<>();


        List<Location> locations = locationRepository.findBySchoolidNotIn(Arrays.asList("999999"));


        locations.forEach(location -> {
            List<LocationSum> lists = new ArrayList<>();

            AtomicInteger contestid = new AtomicInteger(1);


            contestconfigRepository.findAllByOrderByIdAsc().forEach(contestconfig -> {
                LocationSum locationSum = new LocationSum();

                locationSum.setLocation(location.getLocationname());
                AtomicInteger contestidMembers = new AtomicInteger();
                contestidMembers.set(0);
                contestconfig.getContestgroup().forEach(contestitem -> {
                    List<Team> teams = new ArrayList<>();
                    AtomicInteger contestitemMembers = new AtomicInteger(0);

//                    if (!contestitem.matches(excludeItem)) {
                    teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationname(), contestitem);
                    teams.forEach(team -> {
                        contestitemMembers.updateAndGet(n -> n + team.getMembers());
                    });
//                        logger.info(String.format("%s, %s,%s", location.getLocationname(), contestitem, contestidMembers.get()));
                    locationSum.getContestitem().put(contestitem, contestitemMembers.get());

//                    }
                    contestidMembers.updateAndGet(n -> n + contestitemMembers.get());

                });
//                logger.info(String.valueOf(contestid.get()));
                locationSum.setContestid(contestid.get());
                locationSum.setMembers(contestidMembers.get());
                contestid.incrementAndGet();
                lists.add(locationSum);


            });

            List<LocationSum> items = new ArrayList<>();
            locationMp.computeIfAbsent(location.getLocationname(), k -> items);
            lists.forEach(list -> items.add(list));
//            lists.clear();

        });

        ObjectMapper mapper = new ObjectMapper();
        logger.info(mapper.writeValueAsString(locationMp));

        return locationMp;
    }

    @GetMapping(value = "/api/pocketlist/download")
    public ResponseEntity<Resource> getPocketListDownload() throws IOException {


        List<Pocketlist> lists = new ArrayList<>();
//        pocketlistRepository.findAll().forEach(team->lists.add(team));
        pocketlistRepository.findAll().forEach(lists::add);

        String filename = "pocketlist.json";
        //直接輸出


        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
//        DataOutputStream out = new DataOutputStream(resourceStream);
//
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resourceStream, lists);

//        out.writeUTF(mapper.writeValueAsString(lists));


        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s", filename));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);
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


    @GetMapping(value = "/api/pocketlist/inform/all/download")
    public ResponseEntity<Resource> doInformAll(HttpServletRequest request) throws IOException {
        //download pdf


        List<Contestconfig> configs = contestconfigRepository.findAllByOrderByIdAsc();


        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        locations.removeIf(location -> location.getLocationname().equals("未排入"));

        String filename = "inform-all.pdf";


//        List<Team> informTeams = new ArrayList<>();

        List<Inform> informs = new ArrayList<>();

        HashMap<Inform, List<Team>> informAll = new HashMap<>();

        //4 个场次
        configs.forEach(config -> {


            List<String> contestgroup = config.getContestgroup().stream().map(item -> item.toUpperCase() + "組").collect(Collectors.toList());


            locations.forEach(location -> {
                Inform inform = new Inform();

                inform.setContestItem(String.join("、", contestgroup));
                inform.setTeamsize(0);
                inform.setLocation(location.getLocationname());
                inform.setDescription(config.getDescription());
                AtomicReference<Integer> teamsize = new AtomicReference<>(0);
                AtomicReference<Integer> totalpeople = new AtomicReference<>(0);

                config.getContestgroup().forEach(contestitem -> {


                    List<Team> teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationname(), contestitem.toUpperCase());
                    teams.forEach(team -> {
                        if (team.getMembername() != null) {
//                            logger.info(String.format("%s,%s", team.getUsername(), team.getMembername()));
                            totalpeople.updateAndGet(v -> v + 2);
                        } else {
                            totalpeople.updateAndGet(v -> v + 1);
                        }
                    });
//                    inform.getTeams().addAll(teams);
                    teams.forEach(team -> {

                        if (request.getSession(false) == null) {
                            team.setAccount("*****");
                            team.setPasswd("*****");
                        }

                        inform.getTeams().add(team);
                    });
                    teamsize.updateAndGet(v -> v + teams.size());
                });
                inform.setTeamsize(teamsize.get());
                inform.setTotalPeople(totalpeople.get());
                informs.add(inform);

            });

        });


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = doInformCoverAndTeamPDF(informs);
        logger.info("pdf file has been created ");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s", filename));
        headers.setContentType(MediaType.APPLICATION_PDF);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);
    }


    private FontProgram twFontProgram = null;

    ByteArrayOutputStream doInformCoverAndTeamPDF(List<Inform> informs) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc);
        twFontProgram = FontProgramFactory.createFont("/opt/font/TW-Kai-98_1.ttf");

        // Create a PdfFont
        PdfFont font = PdfFontFactory.createFont(twFontProgram, PdfEncodings.IDENTITY_H, false);


        for (int i = 0; i < informs.size(); i++) {

            if (i != 0) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            Paragraph header = new Paragraph();
            header.add(String.format("%s選手帳號密碼", contestHeader)).setFont(font).setBold().setFontSize(29).setTextAlignment(TextAlignment.CENTER);


            document.add(header);

            Paragraph blank = new Paragraph("\n");
            document.add(blank);


            Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();

            table = pdfService.doCoverTablePage(font, informs.get(i));


            document.add(table);


            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));


            table = pdfService.doCover2TablePage(font, informs.get(i).getTeams());
            document.add(table);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            //team inform page
            informs.get(i).getTeams().forEach(team -> {

                Paragraph teamHeader = new Paragraph();
                teamHeader.add(String.format("%s選手帳號密碼通知單", contestHeader)).setFont(font).setBold().setFontSize(29).setTextAlignment(TextAlignment.CENTER);
                document.add(teamHeader);
                try {
                    document.add(pdfService.doTeamTablePage(font, team));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            });

        }


        document.close();


        return baos;
    }


}


//    Table doTeamTablePage(Team team) throws IOException {
//        PdfFont font = PdfFontFactory.createFont(twFont, PdfEncodings.IDENTITY_H, false);
//
//        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
//        Paragraph paragraph = new Paragraph(String.format("試場學校： %s", team.getLocation())).setFont(font);
//        paragraph.add("\n");
//        paragraph.add(String.format("競賽日期：%s", team.getDescription()));
//        paragraph.add("\n");
//        paragraph.add(String.format("競賽項目：%s", team.getContestitem()));
//        Cell cell = new Cell()
//                .setTextAlignment(TextAlignment.LEFT)
//                .setFont(font)
//                .setFontSize(20)
//                .add(paragraph);
//        table.addCell(cell);
//
//        //---------------------------------------------------------------------------------
//
//        paragraph = new Paragraph(String.format("學校：%s", team.getSchoolname())).setFont(font);
//        paragraph.add("\n");
//
//        if (team.getMembername() != null) {
//            paragraph.add(String.format("姓名：%s、%s ", team.getUsername(), team.getMembername()));
//
//        } else {
//            paragraph.add(String.format("姓名：%s ", team.getUsername()));
//
//        }
//
//        paragraph.add("\n");
//        paragraph.add(String.format("帳號：%s ", team.getAccount()));
//        paragraph.add("\n");
//        paragraph.add(String.format("密碼：%s ", team.getPasswd()));
//
//
//        cell = new Cell()
//                .setTextAlignment(TextAlignment.LEFT)
//                .setFont(font)
//                .setFontSize(20)
//                .add(paragraph);
//        table.addCell(cell);
//
//
//        paragraph = new Paragraph(String.format("決賽注意事項")).setFont(font).setTextAlignment(TextAlignment.CENTER).setBold().setUnderline();
//        paragraph.add("\n");
//        String text = "1、「所有競賽項目」的題目於競賽時間開始時自動轉址公布，請記得重新整理網頁，就可看到題目；「所有競賽項目」的作品內容都不得出現姓名及學校等相關資訊。\n" +
//                "2、競賽時間開始後，遲到30分鐘以上不得入場。競賽開始超過40分鐘後，參賽學生始得離開試場。\n" +
//                "3、競賽時間內不得自行攜帶使用任何形式之可攜式儲存媒體及通訊設備，一經發現立即取消參賽資格。（競賽主辦單位借給每位參賽者空白隨身碟一支，供暫存使用）。\n" +
//                "4、競賽時間場地僅能連線至資訊網路應用競賽系統，不提供其他對外之網路連線。競賽時間終了即無法再上傳，參賽者請及早上傳並自行注意時間掌控，避免網路壅塞影響上傳。";
//        Paragraph paragraph2 = new Paragraph(text).setTextAlignment(TextAlignment.LEFT);
//
//        cell = new Cell()
//                .setFont(font)
//                .setFontSize(16)
//                .add(paragraph)
//                .add(paragraph2)
//                .add(new Paragraph("\n"));
//        table.addCell(cell);
//
//        return table;
//    }


//    @GetMapping(value = "/api/pocketlist/inform/team/download")
//    public ResponseEntity<Resource> doInformTeam() throws IOException {
//
//
//        List<Contestconfig> configs = contestconfigRepository.findAllByOrderByIdAsc();
//
//
//        List<Location> locations = new ArrayList<>();
//        locationRepository.findAll().forEach(locations::add);
//        locations.removeIf(location -> location.getLocationname().equals("未排入"));
//
//        List<Team> informTeams = new ArrayList<>();
//
//        //4 个场次
//        configs.forEach(config -> {
//            locations.forEach(location -> {
//                config.getContestgroup().forEach(contestitem -> {
//                    List<Team> teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationname(), contestitem.toUpperCase());
//                    teams.forEach(team -> informTeams.add(team));
//                });
//
//            });
//
//        });
//
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        baos = doPDFTeam(informTeams);
//        logger.info("pdf file has been created ");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add("charset", "utf-8");
//        headers.setContentDispositionFormData("attachment", String.format("%s", "inform-team.pdf"));
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        Resource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
//        return ResponseEntity.ok().headers(headers).body(resource);
//
//    }


//    ByteArrayOutputStream doPDFTeam(List<Team> teams) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
//        Document document = new Document(pdfDoc);
//
//
//        // Create a PdfFont
//        PdfFont font = PdfFontFactory.createFont(twFont, PdfEncodings.IDENTITY_H, false);
//
//
//        for (int i = 0; i < teams.size(); i++) {
//
//            if (i != 0) {
//                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//            }
//
//            Paragraph head = new Paragraph();
//            head.add(String.format("%s選手帳號密碼通知單", contestHeader)).setFont(font).setBold().setFontSize(29).setTextAlignment(TextAlignment.CENTER);
//
//
//            document.add(head);
//
//            Paragraph blank = new Paragraph("\n");
////            document.add(blank);
//
//
//            Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
//
//            Paragraph paragraph = new Paragraph(String.format("試場學校： %s", teams.get(i).getLocation())).setFont(font);
//            paragraph.add("\n");
//            paragraph.add(String.format("競賽日期：%s", teams.get(i).getDescription()));
//            paragraph.add("\n");
//            paragraph.add(String.format("競賽項目：%s", teams.get(i).getContestitem()));
//            Cell cell = new Cell()
//                    .setTextAlignment(TextAlignment.LEFT)
//                    .setFont(font)
//                    .setFontSize(20)
////                    .add(blank)
//                    .add(paragraph);
////                    .add(blank);
//            table.addCell(cell);
//
//            //---------------------------------------------------------------------------------
//
//            paragraph = new Paragraph(String.format("學校：%s", teams.get(i).getSchoolname())).setFont(font);
//            paragraph.add("\n");
//
//            if (teams.get(i).getMembername() != null) {
//                paragraph.add(String.format("姓名：%s、%s ", teams.get(i).getUsername(), teams.get(i).getMembername()));
//
//            } else {
//                paragraph.add(String.format("姓名：%s ", teams.get(i).getUsername()));
//
//            }
//
//            paragraph.add("\n");
//            paragraph.add(String.format("帳號：%s ", teams.get(i).getAccount()));
//            paragraph.add("\n");
//            paragraph.add(String.format("密碼：%s ", teams.get(i).getPasswd()));
//
//
//            cell = new Cell()
//                    .setTextAlignment(TextAlignment.LEFT)
//                    .setFont(font)
//                    .setFontSize(20)
////                    .add(blank)
//                    .add(paragraph);
////                    .add(blank);
//            table.addCell(cell);
//
//
//            paragraph = new Paragraph(String.format("決賽注意事項")).setFont(font).setTextAlignment(TextAlignment.CENTER).setBold().setUnderline();
//            paragraph.add("\n");
//            String text = "1、「所有競賽項目」的題目於競賽時間開始時自動轉址公布，請記得重新整理網頁，就可看到題目；「所有競賽項目」的作品內容都不得出現姓名及學校等相關資訊。\n" +
//                    "2、競賽時間開始後，遲到30分鐘以上不得入場。競賽開始超過40分鐘後，參賽學生始得離開試場。\n" +
//                    "3、競賽時間內不得自行攜帶使用任何形式之可攜式儲存媒體及通訊設備，一經發現立即取消參賽資格。（競賽主辦單位借給每位參賽者空白隨身碟一支，供暫存使用）。\n" +
//                    "4、競賽時間場地僅能連線至資訊網路應用競賽系統，不提供其他對外之網路連線。競賽時間終了即無法再上傳，參賽者請及早上傳並自行注意時間掌控，避免網路壅塞影響上傳。";
//            Paragraph paragraph2 = new Paragraph(text).setTextAlignment(TextAlignment.LEFT);
//
//            cell = new Cell()
////                    .setTextAlignment(TextAlignment.LEFT)
//                    .setFont(font)
//                    .setFontSize(16)
////                    .add(blank)
//                    .add(paragraph)
//                    .add(paragraph2)
//                    .add(blank);
//            table.addCell(cell);
//
//            document.add(table);
//
//        }
//
//
//        document.close();
//
//
//        return baos;
//
//    }

//    @GetMapping(value = "/api/pocketlist/inform/cover/download")
//    public ResponseEntity<Resource> doInformCover() throws IOException {
//
//
////        contestconfigRepository.findAll().forEach(contestconfig -> logger.info(String.valueOf(contestconfig.getId())+contestconfig.getDescription()));
//        List<Contestconfig> configs = contestconfigRepository.findAllByOrderByIdAsc();
//
//
//        List<Location> locations = new ArrayList<>();
//        locationRepository.findAll().forEach(locations::add);
//        locations.removeIf(location -> location.getLocationname().equals("未排入"));
//
//        List<Inform> informs = new ArrayList<>();
//
//        //4 个场次
//        configs.forEach(config -> {
////            logger.info(String.format("%s,%s,%s", config.getId(), config.getContestgroup(), config.getDescription()));
//
//
//            locations.forEach(location -> {
//                Inform inform = new Inform();
//
//                inform.setContestItem(String.join("、", config.getContestgroup()).toUpperCase());
//                inform.setTeamsize(0);
//                inform.setLocation(location.getLocationname());
//                inform.setDescription(config.getDescription());
//                AtomicReference<Integer> teamsize = new AtomicReference<>(0);
//                AtomicReference<Integer> totalpeople = new AtomicReference<>(0);
//
//                config.getContestgroup().forEach(contestitem -> {
//                    List<Team> teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationname(), contestitem.toUpperCase());
//                    teams.forEach(team -> {
//                        if (team.getMembername() != null) {
////                            logger.info(String.format("%s,%s", team.getUsername(), team.getMembername()));
//                            totalpeople.updateAndGet(v -> v + 2);
//                        } else {
//                            totalpeople.updateAndGet(v -> v + 1);
//                        }
//                    });
//                    teamsize.updateAndGet(v -> v + teams.size());
//                });
//                inform.setTeamsize(teamsize.get());
//                inform.setTotalPeople(totalpeople.get());
//                informs.add(inform);
//            });
//
//        });
//
//
//        String filename = "inform-cover.pdf";
//        logger.info("create pdf");
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//
//        baos = doPDFCover(informs);
//        logger.info("pdf file has been created ");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add("charset", "utf-8");
//        headers.setContentDispositionFormData("attachment", String.format("%s", filename));
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        Resource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
//        return ResponseEntity.ok().headers(headers).body(resource);
//    }
//
//    ByteArrayOutputStream doPDFCover(List<Inform> informs) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
//        Document document = new Document(pdfDoc);
//
//
//        // Create a PdfFont
//        PdfFont font = PdfFontFactory.createFont(twFont, PdfEncodings.IDENTITY_H, false);
//
//
//        for (int i = 0; i < informs.size(); i++) {
//
//            if (i != 0) {
//                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//            }
//
//            Paragraph head = new Paragraph();
//            head.add(String.format("%s選手帳號密碼", contestHeader)).setFont(font).setBold().setFontSize(29).setTextAlignment(TextAlignment.CENTER);
//
//
//            document.add(head);
//
//            Paragraph blank = new Paragraph("\n");
//            document.add(blank);
//
//
//            Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
//
//            Paragraph paragraph = new Paragraph(String.format("試場學校： %s", informs.get(i).getLocation())).setFont(font);
//            paragraph.add("\n");
//            paragraph.add(String.format("競賽時間：%s", informs.get(i).getDescription()));
//            Cell cell = new Cell()
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setFont(font)
//                    .setFontSize(28)
//                    .add(blank)
//                    .add(paragraph)
//                    .add(blank);
//            table.addCell(cell);
//
//            paragraph = new Paragraph("項目類別").setFont(font);
//            paragraph.add("\n");
//            paragraph.add(String.format("%s", informs.get(i).getContestItem()));
//
//
//            cell = new Cell()
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setFont(font)
//                    .setFontSize(28)
//                    .add(blank)
//                    .add(paragraph)
//                    .add(blank);
//            table.addCell(cell);
//
//
//            paragraph = new Paragraph(String.format("決賽選手數量：%s 人", informs.get(i).getTotalPeople())).setFont(font);
//            paragraph.add("\n");
//            paragraph.add(String.format("帳號密碼通知單數量：%s 張", informs.get(i).getTeamsize()));
//
//
//            cell = new Cell()
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setFont(font)
//                    .setFontSize(20)
//                    .add(blank)
//                    .add(paragraph)
//                    .add(blank);
//            table.addCell(cell);
//
//
//            document.add(table);
//
//        }
//
//
//        document.close();
//
//
//        return baos;
//    }
