package app.contestTimetable.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrintController {

    @GetMapping(value = "/print")
    public String print() {

        return "print";
    }

}
