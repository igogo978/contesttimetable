package app.contestTimetable.controller;

import app.contestTimetable.service.TeamService;
import app.contestTimetable.storage.StorageProperties;
import app.contestTimetable.storage.StorageService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;


@Controller
public class TeamUploadController {

    private final StorageService storageService;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    StorageProperties storageProperties;
    @Autowired
    TeamService teamService;

    @Value("${multipart.location}")
    private Path path;

    @Autowired
    public TeamUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/teamupload")
    public String uploadTeam() {


        return "teamupload";

    }


    //user upload page
    @PostMapping("/teamupload")
    public String handleTeamsFilesUpload(@RequestParam("files") MultipartFile[] files,
                                         RedirectAttributes redirectAttributes) throws IOException {

        if (files.length == 1 && files[0].getOriginalFilename().toLowerCase(Locale.ROOT).endsWith("json")) {
            MultipartFile file = files[0];
            teamService.restore(file);

        } else {
            logger.info("upload multiple files");
            storageService.store(files);
            if (storageService.store(files)) {
                //update teams and schoolteams
                teamService.updateTeamAndSchoolTeam();
            }
        }
        return "redirect:/team";
    }

//    //讀出team json 上傳檔案內容
//    @RequestMapping(value = "/team/restore", method = RequestMethod.POST)
//    public RedirectView readUploadTeamFile(@RequestParam("file") MultipartFile file, Model model) throws IOException, InvalidFormatException {
//
//        logger.info("filename: " + file.getOriginalFilename());
////        teamService.restore(file);
//        return new RedirectView("/team");
//    }


}
