package app.contestTimetable.repository;

import app.contestTimetable.model.Job;
import app.contestTimetable.model.Ticket;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, String> {
}
