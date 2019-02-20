package app.contestTimetable.repository;

import app.contestTimetable.model.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Long> {


    List<Team> findByContestgroupContaining(String schoolname);

    List<Team> findByContestgroupContainingOrderBySchoolnameDesc(String contestgroup);
//    List<Team> findByContestgroupContainingAndContestgroupContaining(String contestgroup, String level);

    List<Team> findByContestgroupContainingOrderByLocationDesc(String contestgroup);


}
