package app.contestTimetable.service;

import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.SchoolRepository;
import app.contestTimetable.repository.SchoolTeamRepository;
import app.contestTimetable.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchoolTeamService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ContestconfigRepository contestconfigrepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

    @Autowired
    TeamRepository teamrepository;

    public List<SchoolTeam> getSchoolTeams() {


        return schoolTeamRepository.findAllByOrderByMembersDesc();
    }


}
