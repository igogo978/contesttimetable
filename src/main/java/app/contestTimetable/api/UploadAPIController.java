package app.contestTimetable.api;

import app.contestTimetable.model.Ticket;
import app.contestTimetable.service.ReportService;
import app.contestTimetable.service.TicketService;
import app.contestTimetable.service.XlsxService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class UploadAPIController {


    @Value("${multipart.location}")
    private String filepath;

    @Autowired
    ReportService reportservice;

    @Autowired
    XlsxService xlsxService;

    @Autowired
    TicketService ticketService;

    //讀出上傳檔案內容
    @RequestMapping(value = "/report/upload/{filename}", method = RequestMethod.GET)
    public String readUploadReportFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {


        String manualreport = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));

        reportservice.updateTeamLocationAndTicket(manualreport);


        return String.format("上传内容已成功写入资料库");
//        return new String(Base64.getDecoder().decode(filename.getBytes()));


    }

    //讀出上傳檔案內容
    @RequestMapping(value = "/ticket/upload/{filename}", method = RequestMethod.GET)
    public List<Ticket> readUploadTicketFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {


        String ticketxlsx = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));
        List<Ticket> tickets = new ArrayList<>();
        tickets = xlsxService.getTickets(ticketxlsx);
        ticketService.updateTicket(tickets);

//        return String.format("上传内容已成功写入资料库");
        return tickets;


    }


}
