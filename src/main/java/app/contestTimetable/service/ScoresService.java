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

@Service
public class ScoresService {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    XlsxService readxlsx;

    @Autowired
    AreascoreRepository areascoreRepository;


    public void updateAreaScores(String xlsx) throws IOException, InvalidFormatException {
        List<Areascore> areas = new ArrayList<>();

//        List<String> index = new ArrayList<>();

        areas = readxlsx.getAreascore(xlsx);

        //档案中只有单向a->b,需要增加b->a 到资料库中
        List<Areascore> allAreas = new ArrayList<>();
        areas.forEach(area->{
            allAreas.add(area);

            if (!area.getStartarea().equals(area.getEndarea())) {
                Areascore switchArea = new Areascore();

                switchArea.setStartarea(area.getEndarea());
                switchArea.setEndarea(area.getStartarea());
                switchArea.setScores(area.getScores());

                allAreas.add(switchArea);
            }
        });



        //delete records
        areascoreRepository.deleteAll();
        allAreas.forEach(area -> {
            areascoreRepository.save(area);

        });


    }
}
