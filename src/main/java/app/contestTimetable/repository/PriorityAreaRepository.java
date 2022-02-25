package app.contestTimetable.repository;

import app.contestTimetable.model.PriorityArea;
import app.contestTimetable.model.Team;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriorityAreaRepository extends CrudRepository<PriorityArea, String> {


}
