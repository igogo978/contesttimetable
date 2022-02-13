package app.contestTimetable.service;

import app.contestTimetable.model.School;
import app.contestTimetable.repository.SchoolRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class SchoolService {


    Logger logger = LoggerFactory.getLogger(SchoolService.class);

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    XlsxService xlsxService;


    public void update(MultipartFile xlsx) throws IOException, InvalidFormatException {
        List<School> schools = xlsxService.getSchools(xlsx);

        if (schools.size() != 0){
           schoolRepository.deleteAll();
           schools.forEach(school -> schoolRepository.save(school));
        }

    }

//    public void updateSchool(String csvfile) throws IOException, InvalidFormatException {
//        //读取台中市学校名单
////        String tcschool = String.format("%s/%s", settingPath, "tcschool.xlsx");
//
//        List<School> schools = new ArrayList<>();
//        schools = readxlsx.getSchools(csvfile);
//        ArrayList<School> tcschools = new ArrayList<>();
//
//        schoolRepository.deleteAll();
//        schools.forEach(school -> {
//            logger.info(String.format("%s,%s",school.getSchoolid(),school.getSchoolname()));
//            schoolRepository.save(school);
//        });
//    }
}
