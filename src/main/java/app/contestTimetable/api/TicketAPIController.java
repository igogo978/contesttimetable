package app.contestTimetable.api;


import app.contestTimetable.model.Ticket;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.TicketRepository;
import app.contestTimetable.service.TicketService;
import app.contestTimetable.service.XlsxService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TicketAPIController {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TicketService ticketService;
    @Autowired
    XlsxService createxlsx;

    @GetMapping(value = "/api/ticket")
    public List<Ticket> getTickets() {
//        List<Ticket> tickets = new ArrayList<>();
//        ticketRepository.findAll().forEach(ticket -> tickets.add(ticket));
        return ticketService.getAll();

    }


    @GetMapping(value = "/api/ticket/usage")
    public List<Location> getTicketUsage(){
        return ticketService.getTicketUsage();
    }

    @GetMapping(value = "/api/ticket/download")
    public ResponseEntity<Resource> downloadTickets() throws IOException {

        XSSFWorkbook wb = createxlsx.createTickets(getTickets());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        wb.write(byteArrayOutputStream);
        wb.close();

        String filename = "tickets";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        headers.setContentDispositionFormData("attachment", String.format("%s.xlsx", filename));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        return ResponseEntity.ok().headers(headers).body(resource);

    }




}
