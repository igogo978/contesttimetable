package app.contestTimetable.repository;

import app.contestTimetable.model.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Long> {


    List<Team> findByContestitemContaining(String contestitem);

    List<Team> findByContestitemContainingOrderBySchoolnameDesc(String contestgroup);
//    List<Team> findByContestgroupContainingAndContestgroupContaining(String contestgroup, String level);

    List<Team> findByContestitemContainingOrderByLocationDesc(String contestgroup);

    List<Team> findByLocationAndContestitemContainingOrderByLocationDesc(String location, String contestitem);

    List<Team> findAllByOrderBySchoolname();

    List<Team> findAllByOrderByLocation();

    List<Team> findBySchoolname(String schoolname);

    List<Team> findBySchoolnameAndContestitemContaining(String schoolname, String contestitem);

    List<Team> findBySchoolnameAndContestitemContainingOrderByLocation(String schoolname, String contestitem);

    @Query("SELECT DISTINCT t.schoolname FROM Team t WHERE t.location=?1")
    List<String> findDistinctSchoolnameByLocation(String location);

    Integer countByContestitemContainingAndSchoolname(String contestitem, String schoolname);

    //    List<Team> findByContestitemContainingAndSchoolname(String contestitem, String schoolname);

    Integer countByMembernameNotNullAndContestitemContainingAndSchoolname(String contestitem, String schoolname);

}
