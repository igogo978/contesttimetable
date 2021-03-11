package app.contestTimetable.service;


import app.contestTimetable.model.Areascore;
import app.contestTimetable.repository.AreascoreRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScoresService {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    XlsxService readxlsx;

    @Autowired
    AreascoreRepository areascoreRepository;

    public List<Areascore> getSchoolteamAreascores(List<String> schoolteamAreas, List<String> locationAreas) {
        List<Areascore> areascores = new ArrayList<>();

        schoolteamAreas.forEach(schoolteamArea -> {

            locationAreas.forEach(location -> {

//                Areascore areascore = areascoreRepository.findByStartareaAndEndarea(schoolteamArea, location);
                String id = String.format("%s%s", schoolteamArea, location);
                Optional<Areascore> areascore = areascoreRepository.findById(id);

                if (areascore.isPresent()) {
                    areascores.add(areascore.get());
                } else {
                    Areascore areascore1 = new Areascore();
                    areascore1.setStartarea(schoolteamArea);
                    areascore1.setEndarea(location);
                    areascore1.setScores(999.999);

                    logger.info(String.format("%s,%s,%s", areascore1.getStartarea(), areascore1.getEndarea(), areascore1.getScores()));
                    areascores.add(areascore1);
                }


            });

        });

        return areascores;
    }

    public void updateAreaScores(String xlsx) throws IOException, InvalidFormatException {
        List<Areascore> areas = new ArrayList<>();

        areas = readxlsx.getAreascore(xlsx);

        areas.forEach(area -> {


            String id = String.format("%s%s", area.getStartarea(), area.getEndarea());
            logger.info(id);
            Optional<Areascore> optionalAreascore = areascoreRepository.findById(id);

            if (optionalAreascore.isPresent()) {
                Areascore areascore = optionalAreascore.get();
                areascore.setScores(area.getScores());
                areascoreRepository.save(areascore);
            } else {
                Areascore areascore = new Areascore();

                areascore.setId(id);
                areascore.setStartarea(area.getStartarea());

                areascore.setEndarea(area.getEndarea());
                areascore.setScores(area.getScores());
                areascoreRepository.save(areascore);

            }


            id = String.format("%s%s", area.getEndarea(), area.getStartarea());
            logger.info(id);
            optionalAreascore = areascoreRepository.findById(id);

            if (optionalAreascore.isPresent()) {
                Areascore areascore = optionalAreascore.get();
                areascore.setScores(area.getScores());
                areascoreRepository.save(areascore);
            } else {
                Areascore areascore = new Areascore();

                areascore.setId(id);
                areascore.setStartarea(area.getEndarea());

                areascore.setEndarea(area.getStartarea());
                areascore.setScores(area.getScores());
                areascoreRepository.save(areascore);

            }





        });




    }
}
