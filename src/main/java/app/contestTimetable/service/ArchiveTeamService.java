package app.contestTimetable.service;


import app.contestTimetable.model.ArchiveTeam;
import app.contestTimetable.repository.ArchiveTeamRepository;
import app.contestTimetable.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ArchiveTeamService {
    Logger logger = LoggerFactory.getLogger(ArchiveTeamService.class);


    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SchoolTeamService schoolTeamService;

    @Autowired
    ArchiveTeamRepository archiveTeamRepository;

    public Integer getArchiveYear(Integer year) {
      return archiveTeamRepository.findByYearEquals(year).size();
    }

    public void update(Integer year) throws IOException {
        if (getArchiveYear(year) == 0) {
            schoolTeamService.getSchoolTeams().forEach(schoolTeam -> {
                ArchiveTeam archiveTeam = new ArchiveTeam();
                archiveTeam.setYear(year);
                teamRepository.findBySchoolname(schoolTeam.getSchoolname()).forEach(team -> archiveTeam.getTeams().add(team));
                archiveTeam.setSchool(schoolTeam.getSchoolname());
                archiveTeam.setLocation(teamRepository.findBySchoolname(schoolTeam.getSchoolname()).get(0).getLocation());
                archiveTeamRepository.save(archiveTeam);
            });

        } else {
            logger.info(year + " :has data.");
        }

    }


}
