package app.contestTimetable.api;

import app.contestTimetable.model.Team;
import app.contestTimetable.model.archive.ArchiveSchool;
import app.contestTimetable.service.ArchiveTeamService;
import app.contestTimetable.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RestController
public class ArchiveAPIController {


    Logger logger = LoggerFactory.getLogger(ArchiveAPIController.class);

    @Autowired
    ArchiveTeamService archiveTeamService;


    @GetMapping("/api/archive/school/{school}")
    public List<ArchiveSchool> getTeams(@PathVariable String school) {
        return archiveTeamService.getArchiveTeamBySchool(school);
    }


    @GetMapping("/api/archive/school")
    public List<ArchiveSchool> getAll() {
        return archiveTeamService.getAll();
    }

    @GetMapping("/api/archive/download")
    public ResponseEntity<Resource> getAllDownload() throws IOException {
        List<ArchiveSchool> archiveSchools = getAll();
        String filename = "archiveSchools.json";
        //直接輸出
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(byteArrayOutputStream, archiveSchools);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s", filename));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);


    }

    //讀出team json 上傳檔案內容
    @RequestMapping(value = "/api/archive/restore", method = RequestMethod.POST)
    public List<ArchiveSchool> restoreArchives(@RequestParam("jsonfile") MultipartFile jsonfile) throws IOException, InvalidFormatException {

        archiveTeamService.restore(jsonfile);

        return archiveTeamService.getAll();
    }



}
