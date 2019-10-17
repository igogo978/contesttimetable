package app.contestTimetable.controller;

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

import java.util.Base64;


@Controller
public class TeamUploadController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    StorageProperties storageProperties;

    private final StorageService storageService;

    @Autowired
    public TeamUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/team/upload")
    public String uploadTeam() {


        return "teamupload";

    }

    //user upload page
    @PostMapping("/team/upload")
    public String handleTeamsFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        logger.info("filename:" + file.getOriginalFilename());
        String filename = file.getOriginalFilename();
        storageService.store(file);
        return "redirect:/team/upload/" + new String(Base64.getEncoder().encode(filename.getBytes()));
    }


}
