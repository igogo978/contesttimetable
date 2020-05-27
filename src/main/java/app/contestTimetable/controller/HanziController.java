package app.contestTimetable.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HanziController {

    @GetMapping(value = "/hanzi")
    public String getHanziIndex() {

        return "hanzi";
    }
}
