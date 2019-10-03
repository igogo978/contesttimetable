package app.contestTimetable.repository;

import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.model.school.SchoolTeam;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PocketlistRepository extends CrudRepository<Pocketlist, String> {


}
