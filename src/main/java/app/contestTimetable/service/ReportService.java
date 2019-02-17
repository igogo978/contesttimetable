package app.contestTimetable.service;


import app.contestTimetable.model.Report;
import app.contestTimetable.repository.ReportRepository;
import app.contestTimetable.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {


    @Autowired
    ReportRepository reportrepository;

    @Autowired
    TicketRepository ticketrepository;

    public Boolean isExistUuid(String uuid) {

        if (reportrepository.countByUuid(uuid) == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }


    public String updateTicket(Report report) {
        //update ticket
        ticketrepository.deleteAll();
        return report.getReport();

    }

}
