package app.contestTimetable.service;


import app.contestTimetable.model.School;
import app.contestTimetable.model.school.Contestid;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.SchoolRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LocationService {

    Logger logger = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    XlsxService xlsxService;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    ContestconfigRepository contestconfigRepository;

    public List<Location> getLocations(){

        return locationRepository.findBySchoolidNotIn(Arrays.asList("999999"));
    }

    public void update(MultipartFile file) throws IOException, InvalidFormatException {
        //读取场地
        //empty location records

        locationRepository.deleteAll();
        logger.info("设定场地资料");

        ArrayList<Location> locations = new ArrayList<>();
        locations = xlsxService.getLocations(file);

        locations.forEach(location -> {
            School school = schoolRepository.findBySchoolname(location.getLocationName());
            location.setSchoolid(school.getSchoolid());
            contestconfigRepository.findAll().forEach(contestconfig -> {
                Contestid contestid = new Contestid();
                contestid.setContestid(contestconfig.getId());
                contestid.setMembers(location.getCapacity());
                location.getContestids().add(contestid);
            });

        });


        //设定一空试场放无法排入参赛队伍
        Location pending = new Location();
        pending.setLocationName("未排入");
        pending.setSchoolid("999999");
        pending.setCapacity(999);

        contestconfigRepository.findAll().forEach(contestconfig -> {
            Contestid contestid = new Contestid();
            contestid.setContestid(contestconfig.getId());
            contestid.setMembers(999);
            pending.getContestids().add(contestid);
        });


        locations.add(pending);


        //存入location
        locations.forEach(location -> {
            locationRepository.save(location);
        });
    }

//    public void updateLocation(String xlsxfile) throws IOException, InvalidFormatException {
//        //读取场地
//        //empty location records
//
//        locationRepository.deleteAll();
//        logger.info("设定场地资料");
////        String locationfile = String.format("%s/%s", settingPath, "location.xlsx");
//
//        ArrayList<Location> locations = new ArrayList<>();
//        locations = xlsxService.getLocations(xlsxfile);
//
//        locations.forEach(location -> {
//            School school = schoolRepository.findBySchoolname(location.getLocationName());
//            location.setSchoolid(school.getSchoolid());
//            contestconfigRepository.findAll().forEach(contestconfig -> {
//                Contestid contestid = new Contestid();
//                contestid.setContestid(contestconfig.getId());
//                contestid.setMembers(location.getCapacity());
//                location.getContestids().add(contestid);
//            });
//
//        });
//
//
//        //设定一空试场放无法排入参赛队伍
//        Location pending = new Location();
//        pending.setLocationName("未排入");
//        pending.setSchoolid("999999");
//        pending.setCapacity(999);
//
//        contestconfigRepository.findAll().forEach(contestconfig -> {
//            Contestid contestid = new Contestid();
//            contestid.setContestid(contestconfig.getId());
//            contestid.setMembers(999);
//            pending.getContestids().add(contestid);
//        });
//
//
//        locations.add(pending);
//
//
//        //存入location
//        locations.forEach(location -> {
//            locationRepository.save(location);
//        });
//    }
//



}
