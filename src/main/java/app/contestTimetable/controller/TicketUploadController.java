package app.contestTimetable.controller;

import app.contestTimetable.service.TicketService;
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


@Controller
public class TicketUploadController {

    private final StorageService storageService;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TicketService ticketService;
    StorageProperties storageProperties;

    @Autowired
    public TicketUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/ticket/upload")
    public String uploadTicket() {
        return "ticketupload";

    }

    //user upload page
    @PostMapping("/ticket/upload")
    public String handleTicketFileUpload(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException {

        ticketService.update(file);

//        logger.info("filename:" + file.getOriginalFilename());
//        String filename = file.getOriginalFilename();
//        storageService.store(file);
//        return "redirect:/ticket/upload/" + new String(Base64.getEncoder().encode(filename.getBytes()));
        return "redirect:/ticket";
    }


}
