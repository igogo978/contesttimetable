package app.contestTimetable.repository;

import app.contestTimetable.model.Job;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, String> {
}
