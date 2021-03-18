package app.contestTimetable.repository;

import app.contestTimetable.model.Report;
import app.contestTimetable.model.Selectedreport;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface SelectedreportRepository extends CrudRepository<Selectedreport, String> {
//
//    Integer countByContestid(Integer contestid);
//
//    Selectedreport findByContestid(Integer contestid);
}
