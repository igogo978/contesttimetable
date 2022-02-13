package app.contestTimetable.api;

import app.contestTimetable.model.Job;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.service.JobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class JobApiController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JobService jobservice;

    @GetMapping(value = "/job")
    public Job getId() throws JsonProcessingException {
        return jobservice.getJob();
    }

    @GetMapping(value = "/job/client/download",  produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getClientZip () throws IOException {
        String zipfile = "/tmp/contest/client.zip" ;

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename("client.zip")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
        headers.setContentDisposition(contentDisposition);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipfile));
        return ResponseEntity.ok()
                .headers(headers)
//                .header("Content-Disposition", "attachment; filename=client.zip")
                .contentLength(new File(zipfile).length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}
