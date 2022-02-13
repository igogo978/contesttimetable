package app.contestTimetable.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminIndexController {
    @GetMapping(value = "/admin/index")
    public String getAdminIndex() {

        return "adminindex";
    }
}
