package app.contestTimetable.service;

import app.contestTimetable.model.school.Location;
import app.contestTimetable.model.School;
import app.contestTimetable.model.Team;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public ArrayList<Team> getTeams(String docPath) throws IOException {
        ArrayList<Team> allitems = new ArrayList<>();
        ArrayList<List<Team>> groupitems = new ArrayList<>();
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


            try {
                if (matchPresentation.find()) {
                    //簡報多一隊員欄位
                    groupitems.add(readPresentationTeams(xlsx.toString()));

                } else if (matchPainting.find()) {
                    //绘画多一使用软体栏位
                    groupitems.add(readPaintingTeams(xlsx.toString()));
                } else {
                    groupitems.add(readGroupTeams(xlsx.toString()));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //打散塞進allitems裡
            groupitems.forEach(items -> {
                items.forEach(team -> allitems.add(team));
            });
            groupitems.clear();
        });
        return allitems;
    }


    ArrayList<Team> readPaintingTeams(String xlsPath) throws IOException, InvalidFormatException {
        ArrayList<Team> teams = new ArrayList<>();


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
                cell.setCellType(CellType.STRING);

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
            teams.add(team);
        }

        return teams;
    }

    ArrayList<Team> readPresentationTeams(String xlsPath) throws IOException, InvalidFormatException {
        //讀取檔案內容
        ArrayList<Team> teams = new ArrayList<>();


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
                cell.setCellType(CellType.STRING);

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
                        if (value.length() == 0) {
                            value = null;
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

    ArrayList<Team> readGroupTeams(String xlsPath) throws IOException, InvalidFormatException {
        //讀取檔案內容
        ArrayList<Team> teams = new ArrayList<>();

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
                cell.setCellType(CellType.STRING);

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
            teams.add(team);
        }
        return teams;
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
                cell.setCellType(CellType.STRING);

                switch (cell.getColumnIndex()) {
                    case 0:    //第一個欄位, 场地名
                        value = String.valueOf(cell.getStringCellValue());
                        location.setLocationname(value);
                        break;
                    case 1:    //第二個欄位, 容纳人数
                        value = String.valueOf(cell.getStringCellValue());
                        location.setCapacity(Integer.valueOf(value));
                        break;


                    default:
                }
            } //結束讀欄

            locations.add(location);
        }
        return locations;

    }


    public ArrayList<School> getSchools(String docPath) throws IOException, InvalidFormatException {
        //讀取檔案內容
        ArrayList<School> schools = new ArrayList<>();

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
                cell.setCellType(CellType.STRING);

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
                        value = String.valueOf(cell.getStringCellValue());
                        school.setPosition(value);
                        break;
                    case 3:    //第三個欄位, 姓名
                        value = String.valueOf(cell.getStringCellValue());
                        school.setXyposition(value);
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


    public XSSFWorkbook createSelectedReportByLocation(List<Team> teams) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");


        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("序");

        cell = row.createCell(1);
        cell.setCellValue("學校");

        cell = row.createCell(2);
        cell.setCellValue("項目類別");

        cell = row.createCell(3);
        cell.setCellValue(" 姓名 ");

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
            String members = teams.get(i).getUsername();
            if (teams.get(i).getMembername() != null) {
                members = String.format("%s、%s", teams.get(i).getUsername(), teams.get(i).getMembername());
            }
            cell.setCellValue(members);

            cell = row.createCell(4);
            cell.setCellValue(teams.get(i).getLocation());

            cell = row.createCell(5);
            cell.setCellValue(teams.get(i).getDescription());

        }


        return wb;
    }


    public XSSFWorkbook create(ArrayList<String> teams) {
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


}