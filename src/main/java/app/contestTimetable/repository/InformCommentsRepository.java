package app.contestTimetable.repository;

import app.contestTimetable.model.InformComments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InformCommentsRepository extends CrudRepository<InformComments, Integer> {


    List<InformComments> findAllByOrderByIdAsc();
}
