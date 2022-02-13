package app.contestTimetable.repository;

import app.contestTimetable.model.pocketlist.Pocketlist;
import org.springframework.data.repository.CrudRepository;

public interface PocketlistRepository extends CrudRepository<Pocketlist, String> {


}
