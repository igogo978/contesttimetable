package app.contestTimetable.controller;

import app.contestTimetable.storage.StorageProperties;
import app.contestTimetable.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;


@Controller
public class UploadController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    StorageProperties storageProperties;

    private final StorageService storageService;

    @Autowired
    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/report/upload")
    public String uploadReportUuid() {


        return "upload";

    }

    //user upload page
    @PostMapping("/report/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        logger.info("filename:" + file.getOriginalFilename());
        String filename = file.getOriginalFilename();
        storageService.store(file);
        return "redirect:/report/upload/" + new String(Base64.getEncoder().encode(filename.getBytes()));
    }




}