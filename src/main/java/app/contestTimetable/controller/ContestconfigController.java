package app.contestTimetable.controller;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.repository.ContestconfigRepository;
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
public class ContestconfigController {

    @Autowired
    ContestconfigRepository contestconfigRepository;
    Logger logger = LoggerFactory.getLogger(ContestconfigController.class);
    ObjectMapper mapper = new ObjectMapper();


    @GetMapping("/contestconfig")
    public String greeting(Model model) throws JsonProcessingException {
//        model.addAttribute("name", name);
        List<Contestconfig> configs = new ArrayList<>();
        contestconfigRepository.findAll().forEach(configs::add);
//        logger.info(mapper.writeValueAsString(configs.get(0)));

        configs.sort(Comparator.comparing(Contestconfig::getId));

        model.addAttribute("configs", configs);


        return "contestconfig";
    }
}
