package app.contestTimetable.controller;

import app.contestTimetable.service.SchoolService;
import app.contestTimetable.service.XlsxService;
import app.contestTimetable.storage.StorageProperties;
import app.contestTimetable.storage.StorageService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;


@Controller
public class SchoolUploadController {

    Logger logger = LoggerFactory.getLogger(SchoolUploadController.class);

    @Autowired
    SchoolService schoolService;

//    StorageProperties storageProperties;
//    private final StorageService storageService;

//    @Autowired
//    public SchoolUploadController(StorageService storageService) {
//        this.storageService = storageService;
//    }

    @GetMapping(value = "/school/upload")
    public String uploadTeam() {


        return "schoolupload";

    }

    //user upload page
    @PostMapping("/school/upload")
    public String handleTeamsFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException {

        schoolService.update(file);

        return "redirect:/school";
    }


}
