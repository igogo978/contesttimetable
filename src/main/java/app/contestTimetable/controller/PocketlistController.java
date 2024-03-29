package app.contestTimetable.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class PocketlistController {



    @GetMapping(value = "/pocketlist")
    public String getPocketlist() {

        return "pocketlist";
    }


    @GetMapping(value = "/pocketlist/player")
    public String getPocketlistByPlayer() {

        return "pocketlistplayer";
    }

    @GetMapping(value = "/pocketlist/location")
    public String getPocketlistByLocation() {

        return "pocketlistlocation";
    }

}
