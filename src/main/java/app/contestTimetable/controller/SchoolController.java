package app.contestTimetable.controller;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.School;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.SchoolRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class SchoolController {

    @Autowired
    SchoolRepository repository;


    Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper = new ObjectMapper();


    @GetMapping("/school")
    public String greeting(Model model) throws JsonProcessingException {

        List<School> items = new ArrayList<>();
        repository.findAll().forEach(items::add);




        model.addAttribute("items", items);


        return "school";
    }
}
