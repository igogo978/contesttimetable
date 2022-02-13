package app.contestTimetable.controller;

import app.contestTimetable.service.LocationService;
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
public class LocationUploadController {

    Logger logger = LoggerFactory.getLogger(LocationUploadController.class);

    @Autowired
    LocationService locationService;
    StorageProperties storageProperties;

    private final StorageService storageService;


    @Autowired
    public LocationUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/location/upload")
    public String uploadLocation() {
        return "locationupload";

    }

    //user upload page
    @PostMapping("/location/upload")
    public String handleReportFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException {
        logger.info("filename:" + file.getOriginalFilename());

        locationService.update(file);
//        storageService.store(file);
        return "redirect:/location";
    }


}
