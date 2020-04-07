package app.contestTimetable.service;


import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Inform;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    String contestHeader = "臺中市109年度中小學資訊網路應用競賽決賽";

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
                .setFontSize(28)
                .add(blank)
                .add(paragraph)
                .add(blank);
        table.addCell(cell);


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


    public Table doCover2TablePage(PdfFont font, List<Team> teams) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 5, 2, 2})).useAllAvailableWidth();
//        Table table = new Table(UnitValue.createPercentArray(12)).useAllAvailableWidth();


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


        for (int i = 0; i < teams.size(); i++) {
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
                username = new Cell()
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(0)
                        .setFont(font)
                        .setFontSize(11)
                        .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setBorder(Border.NO_BORDER)

                        .add(new Paragraph(String.format("%s,%s", teams.get(i).getUsername(), teams.get(i).getMembername())));

            } else {
                username = new Cell()
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(0)
                        .setFont(font)
                        .setFontSize(11)
                        .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setBorder(Border.NO_BORDER)

                        .add(new Paragraph(teams.get(i).getUsername()));
            }


            table.addCell(username);


        }


        return table;

    }


    public Table doTeamTablePage(PdfFont font, Team team) throws IOException {


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

        if (team.getMembername() != null) {
            paragraph.add(String.format("姓名：%s、%s ", team.getUsername(), team.getMembername()));

        } else {
            paragraph.add(String.format("姓名：%s ", team.getUsername()));

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
        String text = "1、「所有競賽項目」的題目於競賽時間開始時自動轉址公布，請記得重新整理網頁，就可看到題目；「所有競賽項目」的作品內容都不得出現姓名及學校等相關資訊。\n" +
                "2、競賽時間開始後，遲到30分鐘以上不得入場。競賽開始超過40分鐘後，參賽學生始得離開試場。\n" +
                "3、競賽時間內不得自行攜帶使用任何形式之可攜式儲存媒體及通訊設備，一經發現立即取消參賽資格。（競賽主辦單位借給每位參賽者空白隨身碟一支，供暫存使用）。\n" +
                "4、競賽時間場地僅能連線至資訊網路應用競賽系統，不提供其他對外之網路連線。競賽時間終了即無法再上傳，參賽者請及早上傳並自行注意時間掌控，避免網路壅塞影響上傳。";
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
}