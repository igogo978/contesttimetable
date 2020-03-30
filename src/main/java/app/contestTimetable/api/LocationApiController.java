package app.contestTimetable.api;

import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.service.XlsxService;
import com.fasterxml.jackson.databind.ObjectMapper;
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


@RestController
public class LocationApiController {


    @Autowired
    LocationRepository repository;

    @Autowired
    XlsxService createxlsx;


    @RequestMapping("/api/location")
    public List<Location> getItems() {
        List<Location> items = new ArrayList<>();

        repository.findAll().forEach(items::add);

        return items;


    }


    @GetMapping(value = "/api/location/download")
    public ResponseEntity<Resource> downloadReport() throws IOException {

//        ObjectMapper mapper = new ObjectMapper();
        List<Location> locations = new ArrayList<>();

//        repository.findAll().forEach(locations::add);
        locations = repository.findByCapacityLessThan(999);

        String filename = "locations";

        //直接輸出
        XSSFWorkbook wb = createxlsx.createLocations(locations);

        ByteArrayOutputStream resourceStream = new ByteArrayOutputStream();
        wb.write(resourceStream);
        wb.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(resourceStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);

    }


}
