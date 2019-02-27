package app.contestTimetable.service;


import app.contestTimetable.model.*;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ReportService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SelectedreportRepository selectedreportrepository;

    @Autowired
    ReportRepository reportrepository;

    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    TeamRepository teamrepository;

    public Boolean isExistUuid(String uuid) {

        if (reportrepository.countByUuid(uuid) == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }


    public void updateTeamLocation(String manualreport) throws IOException, InvalidFormatException {
        ArrayList<Team> teams = new ArrayList<>();
        //read manual report from xlsx
        Workbook workbook = WorkbookFactory.create(new File(manualreport));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();
        String contestid = null;

        String locationname = "";
        String schoolname = "";
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
                teamrepository.findByContestgroupContaining(groupname.toUpperCase()).forEach(team -> teams.add(team));
            });


            //讀schoolteam
            if (row.getRowNum() > 0) {
                //讀欄 For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                String value = "";

                String level = "";
                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();
                    cell.setCellType(CellType.STRING);

                    switch (cell.getColumnIndex()) {

                        case 0:    //第0個欄位,
                            value = String.valueOf(cell.getStringCellValue());
                            if (value.length() == 6) {
                                level = "location";
                            } else {
                                level = "schoolteam";
                            }

                            break;

                        case 1:
                            value = String.valueOf(cell.getStringCellValue());
                            if (level.equals("location")) {
                                locationname = value;
                            }

                            break;

                        case 2:    //find team schoolname
                            value = String.valueOf(cell.getStringCellValue());
                            if (level.equals("schoolteam")) {
                                schoolname = value;

                            }
                            break;


                        default:
                    }


                } //change row
//                logger.info(String.format("%s:%s,%s", row.getRowNum(),locationname,schoolname));
                for (Team team : teams) {
                    if (team.getSchoolname().equals(schoolname)) {
//                        logger.info(String.format("%s,%s,%s", locationname, team.getSchoolname(),team.getContestgroup()));
                        team.setLocation(locationname);
                    }


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

        Contestconfig config = contestconfigrepository.findById(report.getContestid()).get();

        List<String> contestgroup = config.getContestgroup();
        contestgroup.forEach(groupname -> {
            teamrepository.findByContestgroupContaining(groupname.toUpperCase()).forEach(team -> teams.add(team));
        });


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

    public ArrayList<String> getReport(Report report) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        logger.info(mapper.writeValueAsString(report.getReport()));
        ArrayList<String> teams = new ArrayList<>();
        String contestid = String.format("contestid,%s,   ", report.getContestid());
        teams.add(contestid);
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
                schoolteam.setDistance(school.get("distance").asDouble());

                String team = String.format("%s,%s,%s,%s,%s", "-", schoolteam.getSchoolid(), schoolteam.getSchoolname(), schoolteam.getMembers(), Integer.valueOf(schoolteam.getDistance().intValue()));
                teams.add(team);
            });

        });

        return teams;


    }


}
