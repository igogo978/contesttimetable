package app.contestTimetable.api.scores;


import app.contestTimetable.model.Pocketlist;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Area {

    @GetMapping(value = "/scores/area")
    public String getScoresByArea() throws IOException {


        return "area";
    }
}
