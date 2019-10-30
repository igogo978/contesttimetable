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

        areas = readxlsx.getAreascore(xlsx);

        //empty records
        areascoreRepository.deleteAll();
        areas.forEach(area->{
            areascoreRepository.save(area);
        });

    }
}
