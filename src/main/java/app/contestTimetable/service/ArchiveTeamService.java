package app.contestTimetable.service;


import app.contestTimetable.model.archive.ArchiveSchool;
import app.contestTimetable.model.archive.ArchiveTeam;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.ArchiveSchoolRepository;
import app.contestTimetable.repository.SchoolTeamRepository;
import app.contestTimetable.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArchiveTeamService {
    Logger logger = LoggerFactory.getLogger(ArchiveTeamService.class);


    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

    @Autowired
    SchoolTeamService schoolTeamService;

    @Autowired
    ArchiveSchoolRepository archiveSchoolRepository;

    public Integer getArchiveYear(Integer year) {
        return archiveSchoolRepository.findByYearEquals(year).size();
    }

    public List<ArchiveSchool> getArchiveTeamBySchool(String school) {
        return archiveSchoolRepository.findBySchoolEquals(school);
    }

    public int update(Integer year) throws IOException {
        if (getArchiveYear(year) == 0) {
            logger.info("update teams' data: "+ year);
            schoolTeamService.getSchoolTeams().forEach(schoolTeam -> {
                ArchiveSchool archiveSchool = new ArchiveSchool();
                archiveSchool.setYear(year);
                teamRepository.findBySchoolname(schoolTeam.getSchoolname()).forEach(team -> {
                    ArchiveTeam archiveTeam = new ArchiveTeam();
                    archiveTeam.setContestitem(team.getContestitem());
                    archiveTeam.setUsername(team.getUsername());
                    archiveTeam.setMembername(team.getMembername());
                    archiveTeam.setMembers(team.getMembers());
                    archiveTeam.setInstructor(team.getInstructor());
                    archiveTeam.setDescription(team.getDescription());
                    archiveSchool.getTeams().add(archiveTeam);

                });
                SchoolTeam school = schoolTeamRepository.findBySchoolnameEquals(schoolTeam.getSchoolname());
                archiveSchool.setMembers(school.getMembers());
                String descrition = school.getContestids().stream().map(contestid -> contestid.getContestid() + "-" + contestid.getMembers()).collect(Collectors.joining(", "));
                archiveSchool.setDescription(descrition);

                archiveSchool.setSchool(schoolTeam.getSchoolname());
                archiveSchool.setLocation(teamRepository.findBySchoolname(schoolTeam.getSchoolname()).get(0).getLocation());
                archiveSchoolRepository.save(archiveSchool);
            });

        } else {
            logger.info(year + " :has data.");
        }
        return getArchiveYear(year);

    }


}
