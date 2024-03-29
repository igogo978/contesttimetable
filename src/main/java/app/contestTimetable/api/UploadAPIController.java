package app.contestTimetable.api;

import app.contestTimetable.model.Ticket;
import app.contestTimetable.service.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    LocationService locationService;

    @Autowired
    TeamService teamService;

    @Autowired
    SchoolService schoolService;


    @Autowired
    ScoresService scoresService;

    @Autowired
    PocketlistService pocketlistService;

    @Autowired
    ContestconfigService contestconfigService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //讀出上傳檔案內容
//    @RequestMapping(value = "/report/upload/{filename}", method = RequestMethod.GET)
//    public String readUploadReportFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {
//
//
//        String manualreport = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));
//
//        reportservice.updateTeamLocationAndTicket(manualreport);
//
//
//        return String.format("上传内容已成功写入资料库");
//
//    }

    //讀出上傳檔案內容
    @RequestMapping(value = "/pocketlist/upload/{filename}", method = RequestMethod.GET)
    public RedirectView  restorePocketlistFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {

        String pocketlistfile = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));
        pocketlistService.restorePocketlist(pocketlistfile);

//        return String.format("上传内容已成功写入资料库");
        return new RedirectView("/pocketlist");

    }


    //    //讀出ticket上傳檔案內容
    @RequestMapping(value = "/ticket/upload/{filename}", method = RequestMethod.GET)
    public RedirectView readUploadTicketFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {


        String ticketxlsx = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));
        List<Ticket> tickets = new ArrayList<>();
//        tickets = xlsxService.getTickets(ticketxlsx);
//        ticketService.updateTicket(tickets);

//        return String.format("上传内容已成功写入资料库");
        return new RedirectView("/ticket");

    }

    //讀出contestconfig上傳檔案內容
    @RequestMapping(value = "/contestconfig/upload/{filename}", method = RequestMethod.GET)
    public RedirectView readUploadContestconfigFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {


        String configfile = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));
        logger.info(configfile);

        contestconfigService.updateContestconfig(configfile);

        return new RedirectView("/contestconfig");

    }


    //讀出contestconfig上傳檔案內容
//    @RequestMapping(value = "/school/upload/{filename}", method = RequestMethod.GET)
//    public RedirectView readUploadSchoolFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {
//
//
//        String csvfile = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));
//
////        schoolService.updateSchool(csvfile);
//
//        return new RedirectView("/school");
//
//    }


    //讀出reportrestore上傳檔案內容
    @RequestMapping(value = "/report/restore/upload/{filename}", method = RequestMethod.GET)
    public RedirectView readUploadReportRestoreFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {


        String jsonfile = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));

        reportservice.restoreReports(jsonfile);

        return new RedirectView("/report/");

    }



}
