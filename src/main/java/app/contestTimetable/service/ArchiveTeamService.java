package app.contestTimetable.service;


import app.contestTimetable.model.archive.ArchiveSchool;
import app.contestTimetable.model.archive.ArchiveTeam;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.ArchiveSchoolRepository;
import app.contestTimetable.repository.SchoolTeamRepository;
import app.contestTimetable.repository.TeamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public List<ArchiveSchool> getAll() {
        return StreamSupport.stream(archiveSchoolRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public int update(Integer year) throws IOException {
        if (getArchiveYear(year) == 0) {
            logger.info("update teams' data: " + year);
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

    public void restore(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        ArchiveSchool[] archiveSchools = mapper.readValue(content, ArchiveSchool[].class);

        if (archiveSchools.length != 0) {
            archiveSchoolRepository.deleteAll();
            Arrays.asList(archiveSchools).forEach(archiveSchool -> archiveSchoolRepository.save(archiveSchool));
        }


    }


}
