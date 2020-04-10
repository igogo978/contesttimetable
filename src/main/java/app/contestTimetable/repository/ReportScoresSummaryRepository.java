package app.contestTimetable.repository;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.ReportScoresSummary;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReportScoresSummaryRepository extends CrudRepository<ReportScoresSummary, String> {

    Optional<Report> findByUuid(String uuid);

    Integer countByUuid(String uuid);

    List<ReportScoresSummary> findAllByOrderByScoresAsc();



//    List<Report> findTop20ByContestidOrderByScoresAsc(Integer contestid);
}
