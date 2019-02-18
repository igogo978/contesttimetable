package app.contestTimetable.repository;

import app.contestTimetable.model.Report;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ReportRepository extends CrudRepository<Report, String> {

    Report findByUuid(String uuid);

    Integer countByUuid(String uuid);


    ArrayList<Report> findTop10ByContestidOrderByDistanceAsc(Integer contestid);
}
