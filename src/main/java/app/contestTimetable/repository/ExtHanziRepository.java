package app.contestTimetable.repository;

import app.contestTimetable.model.ExtHanzi;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.Ticket;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExtHanziRepository extends CrudRepository<ExtHanzi, Integer> {

    List<ExtHanzi> findByCharacters(String characters);


}
