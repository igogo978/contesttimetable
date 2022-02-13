package app.contestTimetable.repository;

import app.contestTimetable.model.Areascore;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AreascoreRepository extends CrudRepository<Areascore, String> {

    List<Areascore> findByOrderByStartarea();

    List<Areascore> findByScoresLessThanOrderByStartarea(Double scoresvalues);
    List<Areascore> findByStartarea(String startarea);

    Areascore findByStartareaAndEndarea(String startarea, String endarea);

    List<Areascore> findByIdContaining(String keyword);

}
