package app.contestTimetable.repository;

import app.contestTimetable.model.Inform;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InformRepository extends CrudRepository<Inform, Integer> {


    List<Inform> findAllByOrderByIdAsc();
}
