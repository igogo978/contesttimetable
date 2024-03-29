package app.contestTimetable.controller;

import app.contestTimetable.service.ContestconfigService;
import app.contestTimetable.storage.StorageProperties;
import app.contestTimetable.storage.StorageService;
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
public class ContestconfigUploadController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    StorageProperties storageProperties;

    @Autowired
    ContestconfigService contestconfigService;

    private final StorageService storageService;

    @Autowired
    public ContestconfigUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/contestconfig/upload")
    public String uploadArea() {
        return "contestconfigupload";

    }

    //user upload page
    @PostMapping("/contestconfig/upload")
    public String handleAreaFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {
        logger.info("filename:" + file.getOriginalFilename());
        contestconfigService.update(file);
//        storageService.store(file);
        return "redirect:/contestconfig";
    }


}
