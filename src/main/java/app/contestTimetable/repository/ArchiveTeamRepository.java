package app.contestTimetable.repository;

import app.contestTimetable.model.ArchiveTeam;
import app.contestTimetable.model.Team;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArchiveTeamRepository extends CrudRepository<ArchiveTeam, Long> {

    List<ArchiveTeam> findByYearEquals(Integer year);
}
