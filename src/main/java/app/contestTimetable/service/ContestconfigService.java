package app.contestTimetable.service;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.repository.ContestconfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ContestconfigService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ContestconfigRepository contestconfigRepository;


    public void updateContestconfig(String configfile) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;


        if (new File(configfile).isFile()) {

            contestconfigRepository.deleteAll();

            //create ObjectMapper instance
            logger.info("储存设定档");
            root = mapper.readTree(new File(configfile));
            Contestconfig contestconfig = new Contestconfig();
//            contestconfig.setCount(root.get("count").asInt());

            JsonNode node = root.get("setting");


            for (JsonNode subnode : node) {

                Integer id = subnode.get("id").asInt();

                String description = subnode.get("description").asText();
                contestconfig.setId(id);
                contestconfig.setDescription(description);


                subnode.get("contestgroup").forEach(element -> {
                    contestconfig.getContestgroup().add(element.asText());
                });

                contestconfigRepository.save(contestconfig);
                contestconfig.getContestgroup().clear();
            }
        }


    }
}
