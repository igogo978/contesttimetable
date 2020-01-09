package app.contestTimetable.repository;

import app.contestTimetable.model.school.Location;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationRepository extends CrudRepository<Location, String> {


    List<Location> findByCapacityLessThan(Integer capacityvalue);
}
