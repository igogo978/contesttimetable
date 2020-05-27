package app.contestTimetable.api;


import app.contestTimetable.model.ExtHanzi;
import app.contestTimetable.repository.ExtHanziRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ExtHanziApiController {

    @Autowired
    ExtHanziRepository extHanziRepository;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = "/api/hanzi")
    public List<ExtHanzi> getHanzi() {
        List<ExtHanzi> hanziList = new ArrayList<>();
        extHanziRepository.findAll().forEach(hanziList::add);
        return hanziList;
    }

    @PutMapping(value = "/api/hanzi")
    public List<ExtHanzi> updateHanzi(@RequestBody String payload) throws JsonProcessingException {
        logger.info(payload);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payload);
        String hanzi = root.get("hanzi").asText();

        if (extHanziRepository.findByCharacters(hanzi).size() == 0) {
            ExtHanzi extHanzi = new ExtHanzi();
            extHanzi.setCharacters(hanzi);
            extHanziRepository.save(extHanzi);
        }


        List<ExtHanzi> hanziList = new ArrayList<>();
        extHanziRepository.findAll().forEach(hanziList::add);
        return hanziList;
    }


}
