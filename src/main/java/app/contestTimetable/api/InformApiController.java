package app.contestTimetable.api;


import app.contestTimetable.model.Inform;
import app.contestTimetable.repository.InformRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class InformApiController {

    Logger logger = LoggerFactory.getLogger(InformApiController.class);

    @Autowired
    InformRepository informRepository;

    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/api/inform/comments")
    public Inform getInformComments() {
        Inform inform = new Inform();
        if (informRepository.findById(1).isPresent()) {
            inform = informRepository.findById(1).get();
        } else {
            inform.setId(1);
            inform.setHeader("臺中市OOO年度中小學資訊網路應用競賽決賽");
            List<String> comments = new ArrayList<>();
            comments.add("");
            inform.getComments().add("");
        }
        return inform;
    }

    @PostMapping("/api/inform/comments")
    public Inform updateInform(@RequestBody String payload) throws JsonProcessingException {
        Inform inform = new Inform();
        inform.setId(1);
        ObjectMapper mapper = new ObjectMapper();
        inform = mapper.readValue(payload,Inform.class);
        inform.getComments().removeIf(comment->comment.strip().length() == 0);
        informRepository.save(inform);

        return informRepository.findById(1).get();
    }
}
