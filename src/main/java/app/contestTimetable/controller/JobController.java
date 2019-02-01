package app.contestTimetable.controller;

import app.contestTimetable.model.Job;
import app.contestTimetable.model.SchoolTeam;
import app.contestTimetable.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class JobController {

    @Autowired
    JobService jobservice;

    @GetMapping(value = "/job/{id}")
    public Job getId(@PathVariable("id") int id) {
        return jobservice.getJob(Integer.valueOf(id));
    }


    @GetMapping(value = "/job/{id}/schoolteam")
    public ArrayList<SchoolTeam> getSchoolTeam(@PathVariable("id") int id) {
        ArrayList<SchoolTeam> schoolteams = jobservice.getSchoolteams(Integer.valueOf(id));
        return schoolteams;
    }
}
