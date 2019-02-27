package app.contestTimetable.api;

import app.contestTimetable.service.ReportService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
public class UploadAPIController {


    @Value("${multipart.location}")
    private String filepath;

    @Autowired
    ReportService reportservice;

    //讀出上傳檔案內容
    @RequestMapping(value = "/report/upload/{filename}", method = RequestMethod.GET)
    public String readUploadFile(@PathVariable("filename") String filename, Model model) throws IOException, InvalidFormatException {


        String manualreport = String.format("%s/%s", filepath, new String(Base64.getDecoder().decode(filename.getBytes())));

        reportservice.updateTeamLocation(manualreport);


        return String.format("上传内容已成功写入资料库");
//        return new String(Base64.getDecoder().decode(filename.getBytes()));


    }
}
