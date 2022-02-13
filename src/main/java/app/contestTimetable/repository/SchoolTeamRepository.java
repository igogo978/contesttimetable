package app.contestTimetable.repository;

import app.contestTimetable.model.Team;
import app.contestTimetable.model.school.SchoolTeam;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SchoolTeamRepository extends CrudRepository<SchoolTeam, String> {

    List<SchoolTeam> findAllByOrderByMembersDesc();

    List<SchoolTeam> findByContestidsContestidAndContestidsMembers(Integer contestid, Integer members);
    List<SchoolTeam> findBySchoolnameContaining(String area);
    SchoolTeam findBySchoolnameEquals(String schoolname);
}
