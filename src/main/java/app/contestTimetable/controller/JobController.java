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

    private final StorageService storageService;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    StorageProperties storageProperties;
    @Autowired
    JobService jobservice;
    @Autowired
    ReportRepository reportrepository;

    @Autowired
    public JobController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/job/run")
    public String runReport(Model model) {

        model.addAttribute("client", new File("/tmp/contest/client.zip").exists());

        return "jobrun";

    }


    //user upload page
    @PostMapping("/job/client/upload")
    public String handleReportFileUpload(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes) throws IOException {
        storageService.store(file);
        //move to /tmp/contest/
//        FileUtils.forceMkdir(new File("/tmp/contest"));
        String rootPath = "/tmp/contest";
        File clientZip = new File(String.format("%s/client.zip", rootPath));

        if (!clientZip.exists()) {
            FileUtils.copyFile(new File(String.format("%s/%s",rootPath, file.getOriginalFilename())), clientZip);
        }

        return "redirect:/job/run";
    }


}
