package app.contestTimetable.service;


import app.contestTimetable.model.Team;
import app.contestTimetable.model.report.Report;
import app.contestTimetable.model.report.ReportBody;
import app.contestTimetable.model.report.ReportFull;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    TeamRepository teamrepository;

    @Autowired
    ReportBodyRepository reportBodyRepository;

//    @Autowired
//    ReportSerialRepository reportSerialRepository;

    @Autowired
    TicketService ticketservice;

    public Boolean isExistUuid(String uuid) {

        if (reportRepository.countByUuid(uuid) == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }

    public ReportBody getReportbody(String uuid) {
        if (reportBodyRepository.findByUuid(uuid).isPresent()) {
            return reportBodyRepository.findByUuid(uuid).get();
        }

        return new ReportBody();

    }

//    public  ReportSerial getReportserial(String uuid) {
//        if (reportSerialRepository.findByUuid(uuid).isPresent()) {
//            return reportSerialRepository.findByUuid(uuid).get();
//        }
//        return new ReportSerial();
//    }

    public Report get1stReport() {
        if (reportRepository.findAllByOrderByScoresAsc().size() != 0) {
            return reportRepository.findAllByOrderByScoresAsc().stream()
                    .filter(report -> report.getScores() > 1)
                    .collect(Collectors.toList()).get(0);
        }
        Report report = new Report();
        report.setScores(999999999);
        return report;
    }

    public Optional<Report> getReport(String uuid) {
        return reportRepository.findByUuid(uuid);
    }

    public List<Report> getReports() {
        return reportRepository.findAllByOrderByScoresAsc();
    }

    public List<ReportFull> getReportFulls() {
        List<Report> reports = getReports();
        List<ReportFull> reportFulls = new ArrayList<>();
        reports.forEach(report -> {
            ReportFull reportFull = new ReportFull();
            ReportBody body = reportBodyRepository.findByUuid(report.getUuid()).get();
//            ReportSerial serial = reportSerialRepository.findByUuid(report.getUuid()).get();
            reportFull.setUuid(report.getUuid());
            reportFull.setSummary(report.getSummary());
            reportFull.setScores(report.getScores());
            reportFull.setBody(body);
//            reportFull.setSerial(serial);

            reportFulls.add(reportFull);

        });

        return reportFulls;
    }

    public List<ReportBody> getReportBodies() {
        return reportBodyRepository.findAll();
    }

//    public void updateTeamLocationAndTicket(String manualreport) throws IOException, InvalidFormatException {
//        ArrayList<Team> teams = new ArrayList<>();
//        //read manual report from xlsx
//        Workbook workbook = WorkbookFactory.create(new File(manualreport));
//
//        // Return first sheet from the XLSX  workbook
//        Sheet sheet = workbook.getSheetAt(0);
//
//        // Get iterator to all the rows in current sheet
//        Iterator<Row> rowIterator = sheet.iterator();
//        String contestid = null;
//
//        String level = "";
//
//        Location location = new Location();
//        SchoolTeam schoolteam = new SchoolTeam();
//
//
//        //讀列
//        while (rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//
//            //讀contestid
//            if (row.getRowNum() == 0) {
//                //讀欄 For each row, iterate through each columns
//                Iterator<Cell> cellIterator = row.cellIterator();
//                String value = "";
//                while (cellIterator.hasNext()) {
//                    Cell cell = cellIterator.next();
//
//                    switch (cell.getColumnIndex()) {
//
//                        case 1:    //第二個欄位, contestid value
//                            contestid = String.valueOf(cell.getStringCellValue());
//
//                            break;
//
//
//                        default:
//                    }
//                }
//
//            }
//
//            Contestconfig config = contestconfigrepository.findById(Integer.valueOf(contestid)).get();
//
//            List<String> contestgroup = config.getContestgroup();
//            contestgroup.forEach(groupname -> {
//                teamrepository.findByContestitemContaining(groupname.toUpperCase()).forEach(team -> teams.add(team));
//            });
//
//
//            //讀schoolteam
//            if (row.getRowNum() > 0) {
//                //讀欄 For each row, iterate through each columns
//                Iterator<Cell> cellIterator = row.cellIterator();
//                String value = "";
//
//                while (cellIterator.hasNext()) {
//
//                    Cell cell = cellIterator.next();
//                    cell.setCellType(CellType.STRING);
//
//                    switch (cell.getColumnIndex()) {
//
//                        case 0:    //第0個欄位,
//                            value = String.valueOf(cell.getStringCellValue());
//                            if (value.length() == 6) {
//                                level = "location";
////                                logger.info("locationid:" + value);
//                                location.setSchoolid(value);
//                                //declare a new schoolteam
//                                schoolteam = new SchoolTeam();
//                            } else {
//                                level = "schoolteam";
//                            }
//
//                            break;
//
//                        case 1:
//                            value = String.valueOf(cell.getStringCellValue());
//                            if (level.equals("location")) {
//                                location.setLocationname(value);
//                            }
//                            if (level.equals("schoolteam")) {
//                                schoolteam.setSchoolid(value);
//
//
//                            }
//
//                            break;
//
//                        case 2:    //find team schoolname
//                            value = String.valueOf(cell.getStringCellValue());
//                            if (level.equals("schoolteam")) {
//                                schoolteam.setSchoolname(value);
//
//                            }
//                            break;
//
//
//                        default:
//                    }
//
//
//                } //read column end
//
//
//                if (level.equals("schoolteam")) {
//                    for (Team team : teams) {
//                        if (team.getSchoolname().equals(schoolteam.getSchoolname())) {
//                            team.setLocation(location.getLocationname());
//                        }
//                    }
////                    logger.info(String.format("%s,%s", schoolteam.getSchoolid(), location.getSchoolid()));
//                    ticketservice.updateTicket(schoolteam, location);
//
//                }
//
//
//            }
//
//
//        }
//        logger.info("save upload manual report");
//        teams.forEach(team -> {
////            logger.info(team.getSchoolname() + "," + team.getLocation());
//            teamrepository.save(team);
//        });
//
//    }


    public void updateTeamLocation(ReportBody reportBody) throws IOException {
        //update team's location
        ArrayList<Team> teams = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(reportBody.getBody());

        root.forEach(node -> {
            String location = node.get("location").get("name").asText();
            node.get("teams").forEach(school -> {
//                logger.info(String.format("%s,%s",school.get("name").asText(),location));
                teams.forEach(team -> {
                    if (team.getSchoolname().equals(school.get("name").asText())) {
                        team.setLocation(location);
                        teamrepository.save(team);
                    }
                });

            });
        });

    }


    public Map<Integer, Integer> getReportSummary(ReportBody reportBody) throws IOException {
        Map<Integer, Integer> scoressummary = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> teams = new ArrayList<>();

        JsonNode root = mapper.readTree(reportBody.getBody());
        root.forEach(location -> {
            location.get("teams").forEach(school -> {
                logger.info(String.valueOf(school.get("scores").asInt()));
            });
        });
        return scoressummary;
    }


    public List<String> getReport(ReportBody reportBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(report.getReport()));
        List<String> teams = new ArrayList<>();
//        String contestid = String.format("contestid,%s,   ", report.getContestid());
//        teams.add(contestid);
        JsonNode root = mapper.readTree(reportBody.getBody());
        root.forEach(location -> {
            String locationname = String.format("%s,%s,%s", location.get("location").get("schoolid").asText(), location.get("location").get("name").asText(), location.get("location").get("capacity").asInt());
            teams.add(String.format("%s", locationname));
            location.get("teams").forEach(school -> {
                SchoolTeam schoolteam = new SchoolTeam();
                schoolteam.setSchoolname(school.get("name").asText());
                schoolteam.setSchoolid(school.get("schoolid").asText());
                schoolteam.setMembers(school.get("members").asInt());

//                String team = String.format("%s,%s,%s,%s,%s", "-", schoolteam.getSchoolid(), schoolteam.getSchoolname(), schoolteam.getMembers(), Integer.valueOf(schoolteam.getDistance().intValue()));
//                teams.add(team);
            });

        });

        return teams;

    }


    public void restoreReports(String jsonfile) throws IOException {
        logger.info("reports restore:" + jsonfile);
        ObjectMapper mapper = new ObjectMapper();
        List<ReportFull> reportFulls = Arrays.asList(mapper.readValue(new File(jsonfile), ReportFull[].class));

        reportFulls.forEach(reportFull -> {
            Report report = new Report();
            ReportBody body = reportFull.getBody();
//            ReportSerial serial = reportFull.getSerial();

            report.setUuid(reportFull.getUuid());
            report.setScores(reportFull.getScores());
            report.setSummary(reportFull.getSummary());

            body.setReport(report);
//            serial.setReport(report);
            report.setReportBody(body);
//            report.setReportSerial(serial);

            reportRepository.save(report);

        });

    }

    public void updateReport(Report report) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logger.info(dateTime.format(formatter) + " " + report.getScores());

        String uuid = report.getUuid();
        if (reportRepository.findByUuid(uuid).isPresent()) {
            reportRepository.deleteById(uuid);
        }
        reportRepository.save(report);
    }

    public void updateReport(String payload) throws JsonProcessingException {
        Report report = new Report();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(payload);
        report.setUuid(node.get("uuid").asText());

        DecimalFormat df = new DecimalFormat(".#");
        report.setScores(Double.valueOf(df.format(node.get("scores").asDouble())));
        report.setSummary(node.get("summary").asText());


        ReportBody reportBody = new ReportBody();
        reportBody.setReport(report);
        reportBody.setBody(mapper.writeValueAsString(node.get("candidateList")));

        report.setReportBody(reportBody);
        updateReport(report);
    }

    public void deleteAll() {
        reportRepository.deleteAll();
        logger.info("delete jobs done");
    }

    public void delete(Report report) {
        reportRepository.delete(report);
    }

}
