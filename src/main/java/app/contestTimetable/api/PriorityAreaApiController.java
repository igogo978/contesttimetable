package app.contestTimetable.api;

import app.contestTimetable.model.PriorityArea;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.PriorityAreaRepository;
import app.contestTimetable.service.XlsxService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
public class PriorityAreaApiController {


    @Autowired
    PriorityAreaRepository priorityAreaRepository;



    @RequestMapping("/api/priorityarea")
    public List<PriorityArea> getItems() {
       return StreamSupport.stream(priorityAreaRepository.findAll().spliterator(),false).collect(Collectors.toList());


    }


}
