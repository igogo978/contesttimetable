package app.contestTimetable.controller;

import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.service.JobService;
import app.contestTimetable.storage.StorageProperties;
import app.contestTimetable.storage.StorageService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Controller
public class JobController {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    StorageProperties storageProperties;

    private final StorageService storageService;

    @Autowired
    public JobController(StorageService storageService) {
        this.storageService = storageService;
    }


    @Autowired
    JobService jobservice;

    @Autowired
    ReportRepository reportrepository;

    @GetMapping(value = "/job/run")
    public String runReport(Model model) {

        model.addAttribute("client", new File("/tmp/contest/client.zip").exists());

        return "jobrun";

    }


    //user upload page
    @PostMapping("/job/client/upload")
    public String handleReportFileUpload(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes) throws IOException {
//        logger.info("filename:" + file.getOriginalFilename());
        logger.info("filename:" + "client.zip");
        String filename = file.getOriginalFilename();
        storageService.store(file);
        //move to /tmp/contest/
        FileUtils.forceMkdir(new File("/tmp/contest"));

        FileUtils.copyFile(new File("/tmp/client.zip"), new File("/tmp/contest/client.zip"));
        return "redirect:/job/run";
    }


}
