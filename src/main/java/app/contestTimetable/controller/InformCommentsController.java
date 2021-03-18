package app.contestTimetable.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InformCommentsController {

    @GetMapping(value = "/inform/comments")
    public String getTickets()   {

        return "informcomments";

    }


}
