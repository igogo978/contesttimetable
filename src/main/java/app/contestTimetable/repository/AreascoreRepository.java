package app.contestTimetable.repository;

import app.contestTimetable.model.Areascore;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AreascoreRepository extends CrudRepository<Areascore, Long> {

    List<Areascore> findByOrderByStartarea();


}
