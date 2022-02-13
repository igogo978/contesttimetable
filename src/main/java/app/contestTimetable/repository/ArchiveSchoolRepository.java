package app.contestTimetable.repository;

import app.contestTimetable.model.archive.ArchiveSchool;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArchiveSchoolRepository extends CrudRepository<ArchiveSchool, Long> {

    List<ArchiveSchool> findByYearEquals(Integer year);
    List<ArchiveSchool> findBySchoolEquals(String school);
}
