package app.contestTimetable.repository;

import app.contestTimetable.model.Googlemap;
import app.contestTimetable.model.Report;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface GooglemapRepository extends CrudRepository<Googlemap, String> {

    Optional<Googlemap> findById(String id);

}
