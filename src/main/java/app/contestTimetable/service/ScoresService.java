package app.contestTimetable.service;


import app.contestTimetable.model.Areascore;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.AreascoreRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScoresService {


    Logger logger = LoggerFactory.getLogger(ScoresService.class);

    @Autowired
    XlsxService readxlsx;

    @Autowired
    AreascoreRepository areascoreRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    SchoolTeamService schoolTeamService;

    public List<Areascore> getAllScoresByArea() throws JsonProcessingException {
        List<Areascore> areascores = new ArrayList<>();

        //Step 1.  find location
        List<Location> locations = locationService.getLocations();
        //Step 2. find scores from db
        List<String> areas = schoolTeamService.getAreas();
//        List<Areascore> areascores = new ArrayList<>();
//        areascoreRepository.findAll().forEach(areascores::add);

        locations.forEach(location -> {
            areas.forEach(area -> {
                String id = String.format("%s%s", area, location.getLocationName());
                Areascore areascore = new Areascore();

                if (areascoreRepository.findById(id).isPresent()) {

                    areascore = areascoreRepository.findById(id).get();
                } else {
                    areascore.setId(id);
                    areascore.setStartarea(area);
                    areascore.setEndarea(location.getLocationName());
                    areascore.setScores(999);
                }
                areascores.add(areascore);

            });
        });
        return areascores;
    }


    public List<Areascore> getScoresByAreaname(String areaname) {
        return areascoreRepository.findByStartarea(areaname);
    }

    public List<Areascore> getAreascores() {

        List<String> notSchoolids = new ArrayList<>();
        notSchoolids.add("999999");

        List<Location> locations = locationService.getLocations();
        List<String> locationAreas = new ArrayList<>();

        locations.forEach(location -> {
            locationAreas.add(location.getLocationName().split("(?<=區)")[0]);
        });


        List<String> areas = schoolTeamService.getAreas();
        List<Areascore> areascores = new ArrayList<>();

        areas.forEach(schoolteamArea -> {

            locations.forEach(location -> {

                String locationarea = location.getLocationName().split("(?<=區)")[0];
                String id = String.format("%s%s", schoolteamArea, locationarea);
                Optional<Areascore> areascore = areascoreRepository.findById(id);

                if (areascore.isPresent()) {
                    Areascore area = areascore.get();
                    area.setEndarea(location.getLocationName());
                    areascores.add(area);
                } else {
                    Areascore area = new Areascore();
                    area.setStartarea(schoolteamArea);
                    area.setEndarea(location.getLocationName());
                    area.setScores(999.999);

                    areascores.add(area);
                }

            });

        });

        return areascores;
    }

    public void updateAreaScores(MultipartFile xlsx) throws IOException, InvalidFormatException {
        List<Areascore> areascores = new ArrayList<>();

        areascoreRepository.deleteAll();

        areascores = readxlsx.getAreascore(xlsx);
        logger.info("scores records: " + areascores.size());
        areascores.forEach(area -> {


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

        });


    }
}
