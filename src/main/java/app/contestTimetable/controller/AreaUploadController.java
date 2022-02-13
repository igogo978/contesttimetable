package app.contestTimetable.controller;

import app.contestTimetable.service.ScoresService;
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
public class AreaUploadController {

    Logger logger = LoggerFactory.getLogger(AreaUploadController.class);

    @Autowired
    ScoresService scoresService;

//    StorageProperties storageProperties;
//
//    private final StorageService storageService;
//
//    @Autowired
//    public AreaUploadController(StorageService storageService) {
//        this.storageService = storageService;
//    }

    @GetMapping(value = "/area/upload")
    public String uploadArea() {
        return "areaupload";
    }

    //user upload page
    @PostMapping("/area/upload")
    public String handleAreaFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException {
        logger.info("filename:" + file.getOriginalFilename());
        String filename = file.getOriginalFilename();

        scoresService.updateAreaScores(file);

//        return "redirect:/area/upload/" + new String(Base64.getEncoder().encode(filename.getBytes()));
        return "redirect:/scores/area";
    }


}
