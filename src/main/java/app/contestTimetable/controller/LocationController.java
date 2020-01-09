package app.contestTimetable.controller;


import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.LocationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LocationController {

    @Autowired
    LocationRepository repository;


    Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper = new ObjectMapper();


    @GetMapping("/location")
    public String greeting(Model model) throws JsonProcessingException {

        List<Location> items = new ArrayList<>();

        repository.findAll().forEach(items::add);

//        items.stream().filter(item->!item.getSchoolid().equals("999999"));
        items = items.stream().filter(item -> !item.getSchoolid().equals("999999")).collect(Collectors.toList());
        model.addAttribute("items", items);

        return "location";
    }
}
