package app.contestTimetable.service;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Report;
import app.contestTimetable.model.ReportScoresSummary;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ReportService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    SelectedreportRepository selectedreportrepository;

    @Autowired
    ReportRepository reportrepository;


    @Autowired
    ReportScoresSummaryRepository reportScoresSummaryRepository;

    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    TeamRepository teamrepository;

    @Autowired
    TicketService ticketservice;

    public Boolean isExistUuid(String uuid) {

        if (reportrepository.countByUuid(uuid) == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }


    public void updateTeamLocationAndTicket(String manualreport) throws IOException, InvalidFormatException {
        ArrayList<Team> teams = new ArrayList<>();
        //read manual report from xlsx
        Workbook workbook = WorkbookFactory.create(new File(manualreport));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();
        String contestid = null;

        String level = "";

        Location location = new Location();
        SchoolTeam schoolteam = new SchoolTeam();


        //讀列
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            //讀contestid
            if (row.getRowNum() == 0) {
                //讀欄 For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                String value = "";
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cell.setCellType(CellType.STRING);

                    switch (cell.getColumnIndex()) {

                        case 1:    //第二個欄位, contestid value
                            contestid = String.valueOf(cell.getStringCellValue());

                            break;


                        default:
                    }
                }

            }

            Contestconfig config = contestconfigrepository.findById(Integer.valueOf(contestid)).get();

            List<String> contestgroup = config.getContestgroup();
            contestgroup.forEach(groupname -> {
                teamrepository.findByContestitemContaining(groupname.toUpperCase()).forEach(team -> teams.add(team));
            });


            //讀schoolteam
            if (row.getRowNum() > 0) {
                //讀欄 For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                String value = "";

                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();
                    cell.setCellType(CellType.STRING);

                    switch (cell.getColumnIndex()) {

                        case 0:    //第0個欄位,
                            value = String.valueOf(cell.getStringCellValue());
                            if (value.length() == 6) {
                                level = "location";
//                                logger.info("locationid:" + value);
                                location.setSchoolid(value);
                                //declare a new schoolteam
                                schoolteam = new SchoolTeam();
                            } else {
                                level = "schoolteam";
                            }

                            break;

                        case 1:
                            value = String.valueOf(cell.getStringCellValue());
                            if (level.equals("location")) {
                                location.setLocationname(value);
                            }
                            if (level.equals("schoolteam")) {
                                schoolteam.setSchoolid(value);


                            }

                            break;

                        case 2:    //find team schoolname
                            value = String.valueOf(cell.getStringCellValue());
                            if (level.equals("schoolteam")) {
                                schoolteam.setSchoolname(value);

                            }
                            break;


                        default:
                    }


                } //read column end


                if (level.equals("schoolteam")) {
                    for (Team team : teams) {
                        if (team.getSchoolname().equals(schoolteam.getSchoolname())) {
                            team.setLocation(location.getLocationname());
                        }
                    }
//                    logger.info(String.format("%s,%s", schoolteam.getSchoolid(), location.getSchoolid()));
                    ticketservice.updateTicket(schoolteam, location);

                }


            }


        }
        logger.info("save upload manual report");
        teams.forEach(team -> {
//            logger.info(team.getSchoolname() + "," + team.getLocation());
            teamrepository.save(team);
        });

    }


    public void updateTeamLocation(Report report) throws IOException {
        //update team's location
        ArrayList<Team> teams = new ArrayList<>();
//        Selectedreport selectedreport = selectedreportrepository.findByContestid(report.getContestid());

        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(selectedreport.getReport());
        JsonNode root = mapper.readTree(report.getReport());

//        Contestconfig config = contestconfigrepository.findById(report.getContestid()).get();

//        List<String> contestgroup = config.getContestgroup();
//        contestgroup.forEach(groupname -> {
//            teamrepository.findByContestitemContaining(groupname.toUpperCase()).forEach(team -> teams.add(team));
//        });


        root.forEach(node -> {
            String location = node.get("location").get("name").asText();
//            logger.info(node.get("location").get("name").asText());
            node.get("teams").forEach(school -> {
//                logger.info(String.format("%s,%s",school.get("name").asText(),location));
                teams.forEach(team -> {
                    if (team.getSchoolname().equals(school.get("name").asText())) {
                        team.setLocation(location);
//                        logger.info("save location for team");
                        teamrepository.save(team);
                    }
                });

            });
        });

    }


    public HashMap<Integer, Integer> getReportScoresrange(Report report) throws IOException {
        HashMap<Integer, Integer> scoresrange = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> teams = new ArrayList<>();

        JsonNode root = mapper.readTree(report.getReport());
        root.forEach(location -> {
            location.get("teams").forEach(school -> {
                logger.info(String.valueOf(school.get("scores").asInt()));
            });
        });


        return scoresrange;
    }


    public List<String> getReport(Report report) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(report.getReport()));
        List<String> teams = new ArrayList<>();
//        String contestid = String.format("contestid,%s,   ", report.getContestid());
//        teams.add(contestid);
        JsonNode root = mapper.readTree(report.getReport());
        root.forEach(location -> {
            String locationname = String.format("%s,%s,%s", location.get("location").get("schoolid").asText(), location.get("location").get("name").asText(), location.get("location").get("capacity").asInt());
            teams.add(String.format("%s", locationname));
            location.get("teams").forEach(school -> {
//                logger.info(school.get("name").asText());
                SchoolTeam schoolteam = new SchoolTeam();
                schoolteam.setSchoolname(school.get("name").asText());
                schoolteam.setSchoolid(school.get("schoolid").asText());
                schoolteam.setMembers(school.get("members").asInt());
//                schoolteam.setDistance(school.get("distance").asDouble());

//                String team = String.format("%s,%s,%s,%s,%s", "-", schoolteam.getSchoolid(), schoolteam.getSchoolname(), schoolteam.getMembers(), Integer.valueOf(schoolteam.getDistance().intValue()));
//                teams.add(team);
            });

        });

        return teams;

    }


    public void restoreReportJson(String jsonfile) throws IOException {
        logger.info("report restore:" + jsonfile);
        ObjectMapper mapper = new ObjectMapper();
        List<Report> reports = Arrays.asList(mapper.readValue(new File(jsonfile), Report[].class));



        reports.forEach(report -> {

            ReportScoresSummary reportScoresSummary = new ReportScoresSummary();
            reportScoresSummary.setUuid(report.getUuid());
            reportScoresSummary.setScores(report.getScores());
            reportScoresSummary.setScoresfrequency(report.getScoresfrequency());
            reportrepository.save(report);


        });
    }


}
