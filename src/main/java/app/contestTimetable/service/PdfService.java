package app.contestTimetable.service;


import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Inform;
import app.contestTimetable.repository.*;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    Logger logger = LoggerFactory.getLogger(PdfService.class);

    @Autowired
    ExtHanziRepository extHanziRepository;

    @Autowired
    InformCommentsRepository informCommentsRepository;

    @Autowired
    ContestconfigRepository contestconfigRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    TeamRepository teamRepository;
    private FontProgram twKaiFont = null;


    public PdfService() throws IOException {
    }

    public Table doCoverTablePage(PdfFont font, Inform inform) {

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Paragraph blank = new Paragraph("\n");
        Paragraph paragraph = new Paragraph(String.format("試場學校： %s", inform.getLocation())).setFont(font);
        paragraph.add("\n");
        paragraph.add(String.format("競賽時間：%s", inform.getDescription()));
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
        paragraph.add(String.format("%s", inform.getContestItem()));


        cell = new Cell()
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(26)
                .add(blank)
                .add(paragraph)
                .add(blank);
        table.addCell(cell);


        logger.info("total people:" + inform.getContestItem() + "," + inform.getTotalPeople());
        paragraph = new Paragraph(String.format("決賽選手數量：%s 人", inform.getTotalPeople())).setFont(font);
        paragraph.add("\n");
        paragraph.add(String.format("帳號密碼通知單數量：%s 張", inform.getTeamsize()));


        cell = new Cell()
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(20)
                .add(blank)
                .add(paragraph)
                .add(blank);
        table.addCell(cell);

        return table;
    }

    private String containsExtHanzi(List<String> extHanzis, String name) {

        for (String hanzi : extHanzis) {
            if (name.contains(hanzi)) {
                return hanzi;
            }
        }

        return "";
    }

    private List<Text> getNameTextList(List<String> extHanzis, String name, PdfFont font, PdfFont fontExt) {

        List<Text> usernameTextList = new ArrayList<>();
        // ext part for ext hanzi 0527
        String extHanzi = containsExtHanzi(extHanzis, name);

        Integer hanziPosition = name.indexOf(extHanzi);

        if (hanziPosition == 0) {
//            logger.info("hanzi is in the beginning of name");
//            logger.info("rest part:" + name.split(extHanzi)[1]);
            usernameTextList.add(new Text(extHanzi).setFont(fontExt));
            usernameTextList.add(new Text(name.split(extHanzi)[1]).setFont(font));
        }

        if (hanziPosition != 0 && (hanziPosition + extHanzi.length()) != name.length()) {
//            logger.info("hanzi is in the middle of name");
//
//                    logger.info("1 part: "+ name.split(extHanzi)[0]);
//                    logger.info("2 part: "+ extHanzi);
//                    logger.info("3 part: "+ name.split(extHanzi)[1]);
            usernameTextList.add(new Text(name.split(extHanzi)[0]).setFont(font));
            usernameTextList.add(new Text(extHanzi).setFont(fontExt));
            usernameTextList.add(new Text(name.split(extHanzi)[1]).setFont(font));
        }

        if ((hanziPosition + extHanzi.length()) == name.length()) {
//            logger.info("hanzi is in the end of name");
            usernameTextList.add(new Text(name.split(extHanzi)[0]).setFont(font));
            usernameTextList.add(new Text(extHanzi).setFont(fontExt));
        }
        return usernameTextList;
    }

    public Table doCover2TablePage(PdfFont font, PdfFont fontExt, List<Team> teams) throws IOException {


        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 5, 2, 2})).useAllAvailableWidth();

        Cell sn = new Cell()
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(12)
                .setPadding(0)
                .setMarginRight(0)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph("序"));
        table.addCell(sn);


        Cell location = new Cell()
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(11)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setBorder(Border.NO_BORDER)

                .add(new Paragraph("試場"));
        table.addCell(location);


        Cell contestitem = new Cell()
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(11)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph("項目類別"));
        table.addCell(contestitem);


        Cell schoolname = new Cell()
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(11)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph("學校"));


        table.addCell(schoolname);

        Cell username = new Cell()
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(11)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setBorder(Border.NO_BORDER)

                .add(new Paragraph("姓名"));
        table.addCell(username);

        List<String> extHanzis = new ArrayList<>();
        extHanziRepository.findAll().forEach(hanzi -> {

            extHanzis.add(hanzi.getCharacters());
        });


        for (int i = 0; i < teams.size(); i++) {


            String name = teams.get(i).getUsername();

            List<Text> usernameTextList = new ArrayList<>();
            // ext part for ext hanzi 0527
            String extHanzi = containsExtHanzi(extHanzis, name);

            if (extHanzi.length() != 0) {

                usernameTextList = getNameTextList(extHanzis, name, font, fontExt);

            } else {
                usernameTextList.add(new Text(name).setFont(font));
            }

            List<Text> membernameTextList = new ArrayList<>();
            if (teams.get(i).getMembername() != null) {
                name = teams.get(i).getMembername();
                extHanzi = containsExtHanzi(extHanzis, name);

                if (extHanzi.length() != 0) {

                    membernameTextList = getNameTextList(extHanzis, name, font, fontExt);

                } else {
                    membernameTextList.add(new Text(name).setFont(font));
                }
            }

            //end ext part


            sn = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(0)
                    .setMarginRight(0)
                    .setFont(font)
                    .setFontSize(11)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(String.valueOf(i + 1)));
            table.addCell(sn);


            location = new Cell()
                    .setTextAlignment(TextAlignment.LEFT)
                    .setPadding(0)
                    .setMarginLeft(0)
                    .setFont(font)
                    .setFontSize(11)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setBorder(Border.NO_BORDER)

                    .add(new Paragraph(teams.get(i).getLocation()));
            table.addCell(location);


            contestitem = new Cell()
                    .setTextAlignment(TextAlignment.LEFT)
                    .setPadding(0)
                    .setFont(font)
                    .setFontSize(11)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(teams.get(i).getContestitem()));
            table.addCell(contestitem);


            //the length of schoolname is too long
            if (teams.get(i).getSchoolname().length() > 8) {
                schoolname = new Cell()
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(0)
                        .setMarginRight(0)
                        .setFont(font)
                        .setFontSize(7)
                        .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setBorder(Border.NO_BORDER)
                        .add(new Paragraph(teams.get(i).getSchoolname()));

            } else {

                schoolname = new Cell()
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(0)
                        .setMarginRight(0)
                        .setFont(font)
                        .setFontSize(10)
                        .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setBorder(Border.NO_BORDER)
                        .add(new Paragraph(teams.get(i).getSchoolname()));

            }


            table.addCell(schoolname);


            if (teams.get(i).getMembername() != null) {
                Paragraph fullname = new Paragraph();
                usernameTextList.forEach(hanzi -> {
                    fullname.add(hanzi);

                });
                fullname.add(",");
                membernameTextList.forEach(hanzi -> {
                    fullname.add(hanzi);
                });

                username = new Cell()
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFont(font)
                        .setPadding(0)
                        .setFontSize(11)
                        .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setBorder(Border.NO_BORDER)
                        .add(fullname);

            } else {

                Paragraph fullname = new Paragraph();
                usernameTextList.forEach(hanzi -> {
                    fullname.add(hanzi);

                });

                username = new Cell()
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(0)
                        .setFont(font)
                        .setFontSize(11)
                        .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setBorder(Border.NO_BORDER)
                        .add(fullname);

            }

            table.addCell(username);

        }


        return table;

    }

    public Table doTeamTablePage(PdfFont font, PdfFont fontExt, Team team) throws IOException {
//        //handle unicode 第2字面
//        FontProgram twKaiFontExt = FontProgramFactory.createFont("/opt/font/TW-Kai-Ext-B-98_1.ttf");
//        PdfFont fontExt = PdfFontFactory.createFont(twKaiFontExt, PdfEncodings.IDENTITY_H, true);

        List<String> extHanzis = new ArrayList<>();
        extHanziRepository.findAll().forEach(hanzi -> {

            extHanzis.add(hanzi.getCharacters());
        });


        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Paragraph paragraph = new Paragraph(String.format("試場學校： %s", team.getLocation())).setFont(font);
        paragraph.add("\n");
        paragraph.add(String.format("競賽日期：%s", team.getDescription()));
        paragraph.add("\n");
        paragraph.add(String.format("競賽項目：%s", team.getContestitem()));
        Cell cell = new Cell()
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(20)
                .add(paragraph);
        table.addCell(cell);

        //---------------------------------------------------------------------------------

        paragraph = new Paragraph(String.format("學校：%s", team.getSchoolname())).setFont(font);
        paragraph.add("\n");


        String name = team.getUsername();
        List<Text> usernameTextList = new ArrayList<>();
        // ext part for ext hanzi 0527
        String extHanzi = containsExtHanzi(extHanzis, name);
        if (extHanzi.length() != 0) {
            usernameTextList = getNameTextList(extHanzis, name, font, fontExt);
        } else {
            usernameTextList.add(new Text(name).setFont(font));
        }

        List<Text> membernameTextList = new ArrayList<>();
        if (team.getMembername() != null) {
            name = team.getMembername();
            extHanzi = containsExtHanzi(extHanzis, name);

            if (extHanzi.length() != 0) {
                membernameTextList = getNameTextList(extHanzis, name, font, fontExt);
            } else {
                membernameTextList.add(new Text(name).setFont(font));
            }
        }


        if (team.getMembername() != null) {
//            paragraph.add(String.format("姓名：%s、%s ", team.getUsername(), team.getMembername()));
            Paragraph fullname = new Paragraph();
            fullname.add("姓名：").setFont(font);
            usernameTextList.forEach(hanzi -> {
                fullname.add(hanzi);

            });
            fullname.add(",");
            membernameTextList.forEach(hanzi -> {
                fullname.add(hanzi);
            });


            paragraph.add(fullname);

        } else {
            Paragraph fullname = new Paragraph();
            fullname.add("姓名：").setFont(font);
            usernameTextList.forEach(hanzi -> {
                fullname.add(hanzi);

            });
            paragraph.add(fullname);

        }

        paragraph.add("\n");
        paragraph.add(String.format("帳號：%s ", team.getAccount()));
        paragraph.add("\n");
        paragraph.add(String.format("密碼：%s ", team.getPasswd()));


        cell = new Cell()
                .setTextAlignment(TextAlignment.LEFT)
                .setFont(font)
                .setFontSize(20)
                .add(paragraph);
        table.addCell(cell);


        paragraph = new Paragraph(String.format("決賽注意事項")).setFont(font).setTextAlignment(TextAlignment.CENTER).setBold().setUnderline();
        paragraph.add("\n");

        String text = informCommentsRepository.findById(1).get().getComments()
                .stream()
                .collect(Collectors.joining("\n"));
        Paragraph paragraph2 = new Paragraph(text).setTextAlignment(TextAlignment.LEFT);

        cell = new Cell()
                .setFont(font)
                .setFontSize(16)
                .add(paragraph)
                .add(paragraph2)
                .add(new Paragraph("\n"));
        table.addCell(cell);

        return table;
    }


    public ByteArrayOutputStream doInformCoverAndTeamPDF(List<Inform> informs) throws IOException {
        String contestHeader = informCommentsRepository.findById(1).get().getHeader();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc);

        InputStream inputStream = new ClassPathResource(
                "data/font/TW-Kai-98_1.ttf").getInputStream();

        twKaiFont = FontProgramFactory.createFont(inputStream.readAllBytes());
//        twKaiFont = FontProgramFactory.createFont("/opt/font/TW-Kai-98_1.ttf");

        //handle unicode 第2字面o
        InputStream inputStream2 = new ClassPathResource(
                "data/font/TW-Kai-Ext-B-98_1.ttf").getInputStream();

        FontProgram twKaiFontExt = FontProgramFactory.createFont(inputStream2.readAllBytes());

//        FontProgram twKaiFontExt = FontProgramFactory.createFont("/opt/font/TW-Kai-Ext-B-98_1.ttf");
        PdfFont fontExt = PdfFontFactory.createFont(twKaiFontExt, PdfEncodings.IDENTITY_H, true);

        // Create a PdfFont
        PdfFont font = PdfFontFactory.createFont(twKaiFont, PdfEncodings.IDENTITY_H, true);

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

            table = doCoverTablePage(font, informs.get(i));

            document.add(table);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            table = doCover2TablePage(font, fontExt, informs.get(i).getTeams());
            document.add(table);

            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            //team inform page
            informs.get(i).getTeams().forEach(team -> {

                Paragraph teamHeader = new Paragraph();
                teamHeader.add(String.format("%s選手帳號密碼通知單", contestHeader)).setFont(font).setBold().setFontSize(29).setTextAlignment(TextAlignment.CENTER);
                document.add(teamHeader);
                try {
                    document.add(doTeamTablePage(font, fontExt, team));
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
