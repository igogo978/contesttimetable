package app.contestTimetable.repository;

import app.contestTimetable.model.report.Report;
import app.contestTimetable.model.report.ReportBody;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReportBodyRepository extends CrudRepository<ReportBody, String> {
    Optional<ReportBody> findByUuid(String uuid);
    List<ReportBody> findAll();

}
