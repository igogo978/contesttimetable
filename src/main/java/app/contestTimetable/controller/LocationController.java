package app.contestTimetable.controller;

import app.contestTimetable.model.Location;
import app.contestTimetable.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class LocationController {


    @Autowired
    LocationRepository repository;


    @RequestMapping("/location")
    public List<Location> getItems() {
        List<Location> items = new ArrayList<>();

        repository.findAll().forEach(items::add);

        return items;


    }
}
