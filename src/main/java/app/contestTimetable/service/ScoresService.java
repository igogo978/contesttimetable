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

        List<Areascore> fullareas = new ArrayList<>();
        List<String> index = new ArrayList<>();

        areas = readxlsx.getAreascore(xlsx);

        areas.forEach(area->{
            fullareas.add(area);
            if (!area.getStartarea().equals(area.getEndarea())) {
                Areascore area2 = new Areascore();
                area2.setStartarea(area.getEndarea());
                area2.setEndarea(area.getStartarea());
                area2.setScores(area.getScores());
                fullareas.add(area2);
            }

        });

        //empty records
        areascoreRepository.deleteAll();
        fullareas.forEach(area -> {
            areascoreRepository.save(area);

        });




//        List<Areascore> rAreas = new ArrayList<>();
//        areas.forEach(areascore -> {
//            String i1 = String.format("%s%s", areascore.getStartarea(), areascore.getEndarea());
//            String i2 = String.format("%s%s", areascore.getEndarea(), areascore.getStartarea());
//            if (!index.contains(i1) && !index.contains(i2)) {
//                index.add(i1);
//                System.out.println(String.format("%s,%s,%d",areascore.getStartarea(),areascore.getEndarea(),Math.round(areascore.getScores())));
//            }
//
//        });


    }
}
