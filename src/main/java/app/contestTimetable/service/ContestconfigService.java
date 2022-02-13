package app.contestTimetable.service;


import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.repository.ContestconfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class ContestconfigService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ContestconfigRepository contestconfigRepository;


    public void update(MultipartFile file) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        Contestconfig[] contestconfigs = mapper.readValue(content, Contestconfig[].class);

        if (contestconfigs.length != 0) {
            contestconfigRepository.deleteAll();
            Arrays.asList(contestconfigs).forEach(contestconfig -> contestconfigRepository.save(contestconfig));
        }

        logger.info("储存设定档");

    }

    public void updateContestconfig(String configfile) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;


        if (new File(configfile).isFile()) {

            contestconfigRepository.deleteAll();

            //create ObjectMapper instance
            root = mapper.readTree(new File(configfile));


            Contestconfig contestconfig = new Contestconfig();
//            contestconfig.setCount(root.get("count").asInt());

//            JsonNode node = root.get("setting");


            for (JsonNode node : root) {

                Integer id = node.get("id").asInt();

                String description = node.get("description").asText();
                contestconfig.setId(id);
                contestconfig.setDescription(description);


                node.get("contestgroup").forEach(element -> {
                    contestconfig.getContestgroup().add(element.asText().toUpperCase());
                });

                contestconfigRepository.save(contestconfig);
                contestconfig.getContestgroup().clear();
            }
            logger.info("储存设定档");

        }


    }
}
