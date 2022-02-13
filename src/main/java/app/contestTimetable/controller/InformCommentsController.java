package app.contestTimetable.controller;


import app.contestTimetable.service.InformCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InformCommentsController {

    @GetMapping(value = "/inform/comments")
    public String getInformComments()   {

        return "informcomments";

    }


}
