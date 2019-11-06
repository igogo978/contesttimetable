package app.contestTimetable.api.scores;


import app.contestTimetable.model.Areascore;
import app.contestTimetable.model.Pocketlist;
import app.contestTimetable.repository.AreascoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class Area {

    @Autowired
    AreascoreRepository areascoreRepository;

    @GetMapping(value = "/scores/area")
    public List<Areascore> getScoresByArea() throws IOException {

        List<Areascore> areas = new ArrayList<>();
        areascoreRepository.findAll().forEach(areas::add);
        return areas;
    }
}


