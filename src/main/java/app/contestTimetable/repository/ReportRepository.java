package app.contestTimetable.repository;

import app.contestTimetable.model.Report;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends CrudRepository<Report, String> {

    Optional<Report> findByUuid(String uuid);

    Integer countByUuid(String uuid);

    List<Report> findTop300ByOrderByScoresAsc();
    List<Report> findAllByOrderByScoresAsc();



//    List<Report> findTop20ByContestidOrderByScoresAsc(Integer contestid);
}
