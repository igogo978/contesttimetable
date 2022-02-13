package app.contestTimetable.repository;

import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContestconfigRepository extends CrudRepository<Contestconfig, Integer> {


    List<Contestconfig> findAllByOrderByIdAsc();
}
