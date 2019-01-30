package app.contestTimetable.repository;

import app.contestTimetable.model.School;
import app.contestTimetable.model.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SchoolRepository extends CrudRepository<School, String> {


}
