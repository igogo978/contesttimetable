package app.contestTimetable.service;

import app.contestTimetable.model.School;
import app.contestTimetable.model.Team;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
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
public class ReadXlsxService {

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
                String pattern = ".*(專題簡報).*";

                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);

                // Now create matcher object.
                Matcher m = r.matcher(xlsx.toString());


                try {
                    if (m.find()) {
                        //簡報多一隊員欄位
                        groupitems.add(readPresentationTeams(xlsx.toString()));

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
                            team.setContestgroup(value);
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
                            team.setMember(value);
                            break;
                        case 5:    //第四個欄位, 指導
                            value = String.valueOf(cell.getStringCellValue());
                            team.setInstructor(value);
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
                            team.setContestgroup(value);
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
                        default:
                    }
                } //結束讀欄
                teams.add(team);
            }
            return teams;
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


}
