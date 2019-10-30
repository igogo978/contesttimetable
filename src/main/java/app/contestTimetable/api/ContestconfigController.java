package app.contestTimetable.api;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.School;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ContestconfigController {

    @Autowired
    ContestconfigRepository contestconfigRepository;

    @RequestMapping("/contestconfig")
    public List<Contestconfig> getContestconfig() {
        List<Contestconfig> configs = new ArrayList<>();
        contestconfigRepository.findAll().forEach(configs::add);

        return configs;

    }

}
