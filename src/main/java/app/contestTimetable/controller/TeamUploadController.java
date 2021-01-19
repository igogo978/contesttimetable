package app.contestTimetable.controller;

import app.contestTimetable.service.TeamService;
import app.contestTimetable.storage.StorageProperties;
import app.contestTimetable.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;


@Controller
public class TeamUploadController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    StorageProperties storageProperties;

    private final StorageService storageService;

    @Autowired
    TeamService teamService;

    @Value("${multipart.location}")
    private Path path;

    @Autowired
    public TeamUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/team/upload")
    public String uploadTeam() {


        return "teamupload";

    }

//    //user upload page
//    @PostMapping("/team/upload")
//    public String handleTeamsFileUpload(@RequestParam("file") MultipartFile file,
//                                        RedirectAttributes redirectAttributes) {
//        logger.info("filename:" + file.getOriginalFilename());
//        String filename = file.getOriginalFilename();
//        storageService.store(file);
//        return "redirect:/team/upload/" + new String(Base64.getEncoder().encode(filename.getBytes()));
//    }

    //user upload page
    @PostMapping("/team/upload")
    public String handleTeamsFilesUpload(@RequestParam("files") MultipartFile[] files,
                                         RedirectAttributes redirectAttributes) throws IOException {

        logger.info("upload multiple files");
        storageService.store(files);
        if(storageService.store(files)){
            //update teams
            teamService.update();
        }
        return "redirect:/team";
    }


}
