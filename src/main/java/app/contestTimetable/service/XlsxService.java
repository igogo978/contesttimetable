package app.contestTimetable.service;

import app.contestTimetable.model.Areascore;
import app.contestTimetable.model.School;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.Ticket;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.SchoolRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class XlsxService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SchoolRepository schoolRepository;

    public List<Ticket> getTickets(String xlsxPath) throws IOException, InvalidFormatException {

        List<Ticket> tickets = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(new File(xlsxPath));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        //讀列
        while (rowIterator.hasNext()) {
            Ticket ticket = new Ticket();

            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {  //ommit first row
                continue;
            }

            //讀欄 For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            String value = "";
            while (cellIterator.hasNext()) {
                String schoolname = null;
                String locationname = null;
                String schoolid = null;
                String locationid = null;
                School school = new School();
                Cell cell = cellIterator.next();
//                cell.setCellType(CellType.STRING);


                switch (cell.getColumnIndex()) {
                    case 0:    //第0個欄位, 参赛学校
                        schoolname = String.valueOf(cell.getStringCellValue());
//                        logger.info(schoolname);

                        school = schoolRepository.findBySchoolname(schoolname);
                        ticket.setSchoolname(schoolname);
                        ticket.setSchoolid(school.getSchoolid());

                        break;
                    case 1:    //第1個欄位, 场地
                        locationname = String.valueOf(cell.getStringCellValue());
                        school = schoolRepository.findBySchoolname(locationname);
                        ticket.setLocationid(school.getSchoolid());
                        ticket.setLocationname(locationname);
                        break;


                    default:
                }
            } //結束讀欄

            tickets.add(ticket);
        }


        return tickets;
    }


    public List<Team> getTeams(String docPath) throws IOException {
        List<Team> teams = new ArrayList<>();
        List<List<Team>> groupitems = new ArrayList<>();
        List<Path> xlsxPaths = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(docPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xlsx"))
                    .forEach(xlsxPaths::add);
        }

        xlsxPaths.forEach(xlsx -> {
//            System.out.println(xlsx.toString());
            // String to be scanned to find the pattern.
            String presentation = ".*(專題簡報).*";
            String painting = ".*(電腦繪圖).*";

            // Create a Pattern object
            Pattern presentationregex = Pattern.compile(presentation);

            Pattern paintingregex = Pattern.compile(painting);

            // Now create matcher object.
            Matcher matchPresentation = presentationregex.matcher(xlsx.toString());
            Matcher matchPainting = paintingregex.matcher(xlsx.toString());

            logger.info(xlsx.toString());
            try {
                if (matchPresentation.find()) {
                    logger.info("簡報:" + xlsx.toString());
                    //簡報多一隊員欄位
                    groupitems.add(readPresentationTeams(xlsx.toString()));

                } else if (matchPainting.find()) {
                    //绘画多一使用软体栏位
                    groupitems.add(readPaintingTeams(xlsx.toString()));
                } else {
                    groupitems.add(readContestItemTeams(xlsx.toString()));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //打散塞進items裡
            groupitems.forEach(items -> {
                items.forEach(team -> teams.add(team));
            });
            groupitems.clear();
        });
        return teams;
    }


    List<Team> readPaintingTeams(String xlsPath) throws IOException, InvalidFormatException {
        List<Team> teams = new ArrayList<>();


        Workbook workbook = WorkbookFactory.create(new File(xlsPath));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        //讀列
        while (rowIterator.hasNext()) {
            Team team = new Team();
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {  //ommit first row
                continue;
            }

            //讀欄 For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            String value = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
//                cell.setCellType(CellType.STRING);

                switch (cell.getColumnIndex()) {
                    case 0:    //第0個欄位, 流水號

                        break;
                    case 1:    //第1個欄位, 競賽項目
                        value = String.valueOf(cell.getStringCellValue());
                        team.setContestitem(value);
                        break;
                    case 2:    //第2個欄位, 學校
                        value = String.valueOf(cell.getStringCellValue());
                        team.setSchoolname(value);
                        break;
                    case 3:    //第三個欄位, 姓名
                        value = String.valueOf(cell.getStringCellValue());
                        team.setUsername(value);
                        break;
                    case 4:    //第4個欄位, 組員
                        value = String.valueOf(cell.getStringCellValue());
                        team.setInstructor(value);
                        break;
                    case 5:    //第5個欄位, painting tools
//                        value = String.valueOf(cell.getStringCellValue());
//                        team.setDescription(value);
                        break;
                    case 6:    //第6個欄位, 帐号
                        value = String.valueOf(cell.getStringCellValue());
                        team.setAccount(value);
                        break;
                    case 7:    //第7個欄位, 密码
                        value = String.valueOf(cell.getStringCellValue());
                        team.setPasswd(value);
                        break;

                    default:
                }
            } //結束讀欄
            team.setMembers(1);
            teams.add(team);
        }

        return teams;
    }


    List<Team> readPresentationTeams(String xlsPath) throws IOException, InvalidFormatException {
        //讀取檔案內容
        List<Team> teams = new ArrayList<>();


        Workbook workbook = WorkbookFactory.create(new File(xlsPath));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        //讀列
        while (rowIterator.hasNext()) {
            Team team = new Team();
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {  //ommit first row
                continue;
            }

            //讀欄 For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            String value = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
//                cell.setCellType(CellType.STRING);

                switch (cell.getColumnIndex()) {
                    case 0:    //第一個欄位, 流水號
//                            byte[]  byteArray = cell.getStringCellValue().getBytes(Charset.forName("UTF-8"));
//                            System.out.println(new String(byteArray, "UTF-8"));
//                        System.out.println(String.valueOf(cell.getStringCellValue()));
                        break;
                    case 1:    //第二個欄位, 競賽項目
                        value = String.valueOf(cell.getStringCellValue());
                        team.setContestitem(value);
                        break;
                    case 2:    //第三個欄位, 學校
                        value = String.valueOf(cell.getStringCellValue());
                        team.setSchoolname(value);
                        break;
                    case 3:    //第三個欄位, 姓名
                        value = String.valueOf(cell.getStringCellValue());
                        team.setUsername(value);
                        break;
                    case 4:    //第4個欄位, 組員
                        value = String.valueOf(cell.getStringCellValue());
                        //方便利用资料库计算简报组人数, 空字串不是用notnull
                        team.setMembers(2);

                        if (value.length() == 0) {
                            value = null;
                            team.setMembers(1);

                        }
                        team.setMembername(value);
                        break;
                    case 5:    //第5個欄位, 指導
                        value = String.valueOf(cell.getStringCellValue());
                        team.setInstructor(value);
                        break;
                    case 6:    //第四個欄位, 帐号
                        value = String.valueOf(cell.getStringCellValue());
                        team.setAccount(value);
                        break;
                    case 7:    //第四個欄位, 密码

                        value = String.valueOf(cell.getStringCellValue());
                        team.setPasswd(value);
                        break;

                    default:
                }
            } //結束讀欄
            teams.add(team);
        }

        return teams;
    }

    List<Team> readContestItemTeams(String xlsPath) throws IOException, InvalidFormatException {
        //讀取檔案內容
        List<Team> teams = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(new File(xlsPath));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        //讀列
        while (rowIterator.hasNext()) {
            Team team = new Team();
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {  //ommit first row
                continue;
            }

            //讀欄 For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            String value = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
//                cell.setCellType(CellType.STRING);

                switch (cell.getColumnIndex()) {
                    case 0:    //第一個欄位, 流水號
//                            byte[]  byteArray = cell.getStringCellValue().getBytes(Charset.forName("UTF-8"));
//                            System.out.println(new String(byteArray, "UTF-8"));
//                        System.out.println(String.valueOf(cell.getStringCellValue()));
                        break;
                    case 1:    //第二個欄位, 競賽項目
                        value = String.valueOf(cell.getStringCellValue());
                        team.setContestitem(value);
                        break;
                    case 2:    //第三個欄位, 學校
                        value = String.valueOf(cell.getStringCellValue());
                        team.setSchoolname(value);
                        break;
                    case 3:    //第三個欄位, 姓名
                        value = String.valueOf(cell.getStringCellValue());
                        team.setUsername(value);
                        break;
                    case 4:    //第四個欄位, 指導
                        value = String.valueOf(cell.getStringCellValue());
                        team.setInstructor(value);
                        break;
                    case 5:    //第5個欄位, 帐号
                        value = String.valueOf(cell.getStringCellValue());
                        team.setAccount(value);
                        break;
                    case 6:    //第6個欄位, 密码
                        value = String.valueOf(cell.getStringCellValue());
                        team.setPasswd(value);
                        break;
                    default:
                }
            } //結束讀欄
            team.setMembers(1);
            teams.add(team);
        }
        return teams;
    }


    public List<Areascore> getAreascore(String xlsx) throws IOException, InvalidFormatException {
        List<Areascore> areas = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(new File(xlsx));
        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);


        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();


        //讀列
        while (rowIterator.hasNext()) {
            Areascore areascore = new Areascore();
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {  //ommit first row
                continue;
            }

            //讀欄 For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            String value = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
//                cell.setCellType(CellType.STRING);
                areascore.setScores(999.999);
                switch (cell.getColumnIndex()) {
                    case 0:    //第一個欄位, 起
                        value = String.valueOf(cell.getStringCellValue());
                        areascore.setStartarea(value);
                        break;
                    case 1:    //第二個欄位, 迄
                        value = String.valueOf(cell.getStringCellValue());
                        areascore.setEndarea(value);
                        break;

                    case 2:    //第二個欄位, 得分
                        value = String.valueOf(cell.getStringCellValue());
                        if (value.length() != 0) {
                            areascore.setScores(Double.valueOf(value));

                        }

                        break;


                    default:
                }
            } //結束讀欄

//            if (areascore.getScores() != 9999.9999) {
////                logger.info(String.format("%s,%s,%f", areascore.getStartarea(), areascore.getEndarea(), areascore.getScores()));
//            } else {
//                logger.info(String.format("%s-%s 无记录", areascore.getStartarea(), areascore.getEndarea()));
//
//            }
//            if (areascore.getStartarea().equals(areascore.getEndarea())) {
//                areascore.setScores(1);
//            }
            areas.add(areascore);

        }


        return areas;
    }


    public ArrayList<Location> getLocations(String docPath) throws IOException, InvalidFormatException {
        //讀取檔案內容
        ArrayList<Location> locations = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(new File(docPath));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        //讀列
        while (rowIterator.hasNext()) {
            Location location = new Location();
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {  //ommit first row
                continue;
            }

            //讀欄 For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            String value = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
//                cell.setCellType(CellType.STRING);

                switch (cell.getColumnIndex()) {
                    case 0:    //第一個欄位, 场地名
                        value = String.valueOf(cell.getStringCellValue());
                        location.setLocationname(value);
                        break;
                    case 1:    //第二個欄位, 容纳人数
                        // string 29.0 -> double 29.0 -> int 29
                        Integer capacity = (int) Double.parseDouble(cell.getStringCellValue());
                        location.setCapacity(capacity);
                        break;


                    default:
                }
            } //結束讀欄

            locations.add(location);
        }
        return locations;

    }


    public List<School> getSchools(String docPath) throws IOException, InvalidFormatException {
        //讀取檔案內容
        List<School> schools = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(new File(docPath));

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();

        //讀列
        while (rowIterator.hasNext()) {
            School school = new School();
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {  //ommit first row
                continue;
            }

            //讀欄 For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            String value = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
//                cell.setCellType(CellType.STRING);

                switch (cell.getColumnIndex()) {
                    case 0:    //第一個欄位, 機關代碼
//                            byte[]  byteArray = cell.getStringCellValue().getBytes(Charset.forName("UTF-8"));
//                            System.out.println(new String(byteArray, "UTF-8"));
//                        System.out.println(String.valueOf(cell.getStringCellValue()));
                        value = String.valueOf(cell.getStringCellValue());

                        school.setSchoolid(value);
                        break;
                    case 1:    //第二個欄位, 競賽項目
                        value = String.valueOf(cell.getStringCellValue());
                        school.setSchoolname(value);
                        break;
                    case 2:    //第三個欄位, 學校
//                        value = String.valueOf(cell.getStringCellValue());
//                        school.setPosition(value);
                        break;
                    case 3:    //第三個欄位, 姓名
//                        value = String.valueOf(cell.getStringCellValue());
//                        school.setXyposition(value);
                        break;

                    default:
                }
            } //結束讀欄
            schools.add(school);
        }
        return schools;

    }


    public XSSFWorkbook createSelectedReport(List<Team> teams) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("序");

        cell = row.createCell(1);
        cell.setCellValue("試場 ");

        cell = row.createCell(2);
        cell.setCellValue("項目類別");

        cell = row.createCell(3);
        cell.setCellValue("學校");

        cell = row.createCell(4);
        cell.setCellValue("姓名");

        for (int i = 0; i < teams.size(); i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(i + 1);


            cell = row.createCell(1);
            cell.setCellValue(teams.get(i).getLocation());


            cell = row.createCell(2);
            cell.setCellValue(teams.get(i).getContestitem());

            cell = row.createCell(3);
            cell.setCellValue(teams.get(i).getSchoolname());

            cell = row.createCell(4);
            String members = teams.get(i).getUsername();
            if (teams.get(i).getMembername() != null) {
                members = String.format("%s、%s", teams.get(i).getUsername(), teams.get(i).getMembername());
            }
            cell.setCellValue(members);

        }


        return wb;
    }


    public XSSFWorkbook createPocketlistByLocation(List<Team> teams) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("序");

        cell = row.createCell(1);
        cell.setCellValue("試場");

        cell = row.createCell(2);
        cell.setCellValue("項目類別");

        cell = row.createCell(3);
        cell.setCellValue("學校");

        cell = row.createCell(4);
        cell.setCellValue("姓名");

//        cell = row.createCell(5);
//        cell.setCellValue("時間");

        for (int i = 0; i < teams.size(); i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(i + 1);

            cell = row.createCell(1);
            cell.setCellValue(teams.get(i).getLocation());


            cell = row.createCell(2);
            cell.setCellValue(teams.get(i).getContestitem());


            cell = row.createCell(3);
            cell.setCellValue(teams.get(i).getSchoolname());


            cell = row.createCell(4);
            String members = teams.get(i).getUsername();
            if (teams.get(i).getMembername() != null) {
                members = String.format("%s、%s", teams.get(i).getUsername(), teams.get(i).getMembername());
            }
            cell.setCellValue(members);


//            cell = row.createCell(5);
//            cell.setCellValue(teams.get(i).getDescription());

        }


        return wb;
    }


    public XSSFWorkbook createPocketlistByPlayer(List<Team> teams) {
        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);


        //title
        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("序");

        cell = row.createCell(1);
        cell.setCellValue("學校");


        cell = row.createCell(2);
        cell.setCellValue("項目類別");

        cell = row.createCell(3);
        cell.setCellValue("姓名");


        cell = row.createCell(4);
        cell.setCellValue("試場");

        cell = row.createCell(5);
        cell.setCellValue("時間");


        for (int i = 0; i < teams.size(); i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(i + 1);

            cell = row.createCell(1);
            cell.setCellValue(teams.get(i).getSchoolname());


            cell = row.createCell(2);
            cell.setCellValue(teams.get(i).getContestitem());


            cell = row.createCell(3);
            if (teams.get(i).getMembername() != null) {
                cell.setCellValue(String.format("%s、%s", teams.get(i).getUsername(), teams.get(i).getMembername()));
            } else {
                cell.setCellValue(teams.get(i).getUsername());
            }


            cell = row.createCell(4);
            cell.setCellValue(teams.get(i).getLocation());


            cell = row.createCell(5);
            cell.setCellValue(teams.get(i).getDescription());


        }

        return wb;
    }


    public XSSFWorkbook createAreascores(List<Areascore> areas) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);


        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("A點");

        cell = row.createCell(1);
        cell.setCellValue("B點");

        cell = row.createCell(2);
        cell.setCellValue("得分");

        cell = row.createCell(2);
        cell.setCellValue("兩點互換距離一樣，空值代表该区没有试场");

        for (int i = 0; i < areas.size(); i++) {

            row = sheet.createRow(i + 1);


            cell = row.createCell(0);
            cell.setCellValue(areas.get(i).getStartarea());

            cell = row.createCell(1);
            cell.setCellValue(areas.get(i).getEndarea());


            //超过999代表原来得分表中没有填值,该区没有试场
            cell = row.createCell(2);
            if (areas.get(i).getScores() < 999) {
                cell.setCellValue(areas.get(i).getScores());

            } else {
                cell.setCellValue("");
            }


        }

        return wb;

    }


    public XSSFWorkbook createLocations(List<Location> locations) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);


        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("學校名稱");

        cell = row.createCell(1);
        cell.setCellValue("電腦數");


        for (int i = 0; i < locations.size(); i++) {

            row = sheet.createRow(i + 1);


            cell = row.createCell(0);
            cell.setCellValue(locations.get(i).getLocationname());

            cell = row.createCell(1);

            cell.setCellValue(locations.get(i).getCapacity());


        }

        return wb;

    }


    public XSSFWorkbook createTickets(List<Ticket> tickets) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);


        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("參賽學校");

        cell = row.createCell(1);
        cell.setCellValue("場地");


        for (int i = 0; i < tickets.size(); i++) {

            row = sheet.createRow(i + 1);


            cell = row.createCell(0);
            cell.setCellValue(tickets.get(i).getSchoolname());

            cell = row.createCell(1);

            cell.setCellValue(tickets.get(i).getLocationname());


        }

        return wb;

    }


    public XSSFWorkbook create(List<String> teams) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);


        for (int i = 0; i < teams.size(); i++) {
            String[] team = teams.get(i).split(",");

            row = sheet.createRow(i);

            cell = row.createCell(0);
            cell.setCellValue(team[0]);

            cell = row.createCell(1);
            cell.setCellValue(team[1]);

            cell = row.createCell(2);
            cell.setCellValue(team[2]);


            //team under location
            if (team.length > 3) {
                cell = row.createCell(3);
                cell.setCellValue(team[3]);

                cell = row.createCell(4);
                cell.setCellValue(team[4]);

            }

        }

        return wb;

    }



    public XSSFWorkbook createPocketlistInformLocation(List<Team> teams) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("序");

        cell = row.createCell(1);
        cell.setCellValue("試場 ");

        cell = row.createCell(2);
        cell.setCellValue("項目類別");

        cell = row.createCell(3);
        cell.setCellValue("學校");

        cell = row.createCell(4);
        cell.setCellValue("姓名");

        cell = row.createCell(5);
        cell.setCellValue("時間");

        for (int i = 0; i < teams.size(); i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(i + 1);


            cell = row.createCell(1);
            cell.setCellValue(teams.get(i).getLocation());


            cell = row.createCell(2);
            cell.setCellValue(teams.get(i).getContestitem());

            cell = row.createCell(3);
            cell.setCellValue(teams.get(i).getSchoolname());

            cell = row.createCell(4);
            String members = teams.get(i).getUsername();
            if (teams.get(i).getMembername() != null) {
                members = String.format("%s、%s", teams.get(i).getUsername(), teams.get(i).getMembername());
            }
            cell.setCellValue(members);

            cell = row.createCell(5);
            cell.setCellValue(teams.get(i).getDescription());

        }


        return wb;
    }



}
