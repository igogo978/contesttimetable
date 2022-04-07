package app.contestTimetable.repository;

import app.contestTimetable.model.Hanzi;
import org.springframework.data.repository.CrudRepository;

public interface HanziRepository extends CrudRepository<Hanzi,String> {
    Hanzi findByHanzi(String hanzi);
}
