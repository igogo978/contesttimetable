package app.contestTimetable.controller;


import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ArchiveController {

    @GetMapping(value = "/archive/school/{school}")
    public String getArchiveSchool(Model model, @PathVariable("school") String school)   {
        model.addAttribute("school", school);
        return "archiveschool";
    }

}
