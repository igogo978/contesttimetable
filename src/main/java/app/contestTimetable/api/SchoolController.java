package app.contestTimetable.api;


import app.contestTimetable.model.School;
import app.contestTimetable.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SchoolController {

    @Autowired
    SchoolRepository repository;

    @RequestMapping("/school")
    public List<School> getSchool() {
        List<School> items = new ArrayList<>();

        repository.findAll().forEach(items::add);

        return items;
    }

}
