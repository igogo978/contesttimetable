package app.contestTimetable.api;

import app.contestTimetable.model.archive.ArchiveSchool;
import app.contestTimetable.service.ArchiveTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArchiveAPIController {

    @Autowired
    ArchiveTeamService archiveTeamService;


    @GetMapping("/api/archive/school/{school}")
    List<ArchiveSchool> getTeams(@PathVariable String school) {

        return archiveTeamService.getArchiveTeamBySchool(school);
    }
}
