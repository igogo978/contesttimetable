package app.contestTimetable.controller.scores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AreaController {

//    Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping(value = "/scoresarea")
    public String getScoresAreaPage() {

        return "area";
    }


}
