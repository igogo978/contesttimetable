package app.contestTimetable.api;


import app.contestTimetable.model.InformComments;
import app.contestTimetable.service.InformCommentsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InformApiController {

    Logger logger = LoggerFactory.getLogger(InformApiController.class);

    @Autowired
    InformCommentsService informCommentsService;
    ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/api/inform/comments")
    public InformComments getInformComments() {

        InformComments informComments = new InformComments();
        informComments = informCommentsService.getInformComments();

        return informComments;
    }

    @PostMapping("/api/inform/comments")
    public InformComments updateInform(@RequestBody String payload) throws JsonProcessingException {
        return informCommentsService.update(payload);
    }
}
