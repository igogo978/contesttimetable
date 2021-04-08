package app.contestTimetable.service;


import app.contestTimetable.model.report.Report;
import app.contestTimetable.model.Ticket;
import app.contestTimetable.model.report.ReportBody;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.model.school.SchoolTeam;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.SchoolTeamRepository;
import app.contestTimetable.repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TicketService {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TicketRepository ticketrepository;

    @Autowired
    SchoolTeamRepository schoolTeamRepository;

    @Autowired
    LocationRepository locationRepository;

    public List<Ticket> getAll() {

        List<Ticket> tickets = new ArrayList<>();
        ticketrepository.findAll().forEach(school -> tickets.add(school));
        return tickets;
    }

    public List<Location> getTicketUsage() {
        List<Ticket> tickets = getAll();
        List<Location> locations = new ArrayList<>();
        List<Location> usage = new ArrayList<>();
        locationRepository.findAll().forEach(location -> {
            if (!location.getSchoolid().equals("999999")) {
                locations.add(location);
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        locations.forEach(location -> {
            List<SchoolTeam> schoolTeams = new ArrayList<>();
//            AtomicInteger users1 = new AtomicInteger(0);
            ConcurrentHashMap<Integer, Integer> contestusers = new ConcurrentHashMap<>();
            contestusers.put(1, 0);
            contestusers.put(2, 0);
            contestusers.put(3, 0);
            contestusers.put(4, 0);
            //home users
            SchoolTeam home = schoolTeamRepository.findBySchoolnameEquals(location.getLocationname());
            if (home != null) {
                int users1 = home.getContestids().stream().filter(contest -> contest.getContestid() == 1).findFirst().get().getMembers();
                int users2 = home.getContestids().stream().filter(contest -> contest.getContestid() == 2).findFirst().get().getMembers();
                int users3 = home.getContestids().stream().filter(contest -> contest.getContestid() == 3).findFirst().get().getMembers();
                int users4 = home.getContestids().stream().filter(contest -> contest.getContestid() == 4).findFirst().get().getMembers();

                contestusers.computeIfPresent(1, (k, v) -> v + users1);
                contestusers.computeIfPresent(2, (k, v) -> v + users2);
                contestusers.computeIfPresent(3, (k, v) -> v + users3);
                contestusers.computeIfPresent(4, (k, v) -> v + users4);
            }

            tickets.forEach(ticket -> {
                if (location.getLocationname().equals(ticket.getLocationname())) {
                    SchoolTeam school = schoolTeamRepository.findBySchoolnameEquals(ticket.getSchoolname());
                    if (school != null) {
                        int users1 = school.getContestids().stream().filter(contest -> contest.getContestid() == 1).findFirst().get().getMembers();
                        int users2 = school.getContestids().stream().filter(contest -> contest.getContestid() == 2).findFirst().get().getMembers();
                        int users3 = school.getContestids().stream().filter(contest -> contest.getContestid() == 3).findFirst().get().getMembers();
                        int users4 = school.getContestids().stream().filter(contest -> contest.getContestid() == 4).findFirst().get().getMembers();

                        contestusers.computeIfPresent(1, (k, v) -> v + users1);
                        contestusers.computeIfPresent(2, (k, v) -> v + users2);
                        contestusers.computeIfPresent(3, (k, v) -> v + users3);
                        contestusers.computeIfPresent(4, (k, v) -> v + users4);
                    }
                }
            });
            location.getContestids().forEach(contest->{
                if (contest.getContestid()==1) {
                    contest.setMembers(contest.getMembers()-contestusers.get(1));
                }

                if (contest.getContestid()==2) {
                    contest.setMembers(contest.getMembers()-contestusers.get(2));
                }

                if (contest.getContestid()==3) {
                    contest.setMembers(contest.getMembers()-contestusers.get(3));
                }
                if (contest.getContestid()==4) {
                    contest.setMembers(contest.getMembers()-contestusers.get(4));
                }
            });

        });

        return locations;
    }

    public void updateTicket(List<Ticket> tickets) {
        logger.info("update tickets");
        ticketrepository.deleteAll();
        tickets.forEach(ticket -> {
            ticketrepository.save(ticket);
        });

    }


//    public void updateTicket(ReportBody reportBody) throws IOException {
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(reportBody.getBody());
//
//        root.forEach(candidate -> {
//            String locationid = candidate.get("location").get("schoolid").asText();
//            JsonNode node = candidate.get("teams");
//            node.forEach(school -> {
//                String schoolid = school.get("schoolid").asText();
//
//                if (ticketrepository.countBySchoolid(schoolid) == 0) {
//                    logger.info(String.format("schoolid: %s  update ticket", schoolid));
//                    Ticket ticket = new Ticket();
//                    ticket.setLocationid(locationid);
//                    ticket.setSchoolid(schoolid);
//
//                    ticketrepository.save(ticket);
//
//                }
//
//
//            });
//        });
//
//    }

    public void updateTicket(SchoolTeam schoolteam, Location location) throws IOException {
        if (ticketrepository.countBySchoolid(schoolteam.getSchoolid()) == 0) {
            Ticket ticket = new Ticket();
            ticket.setSchoolid(schoolteam.getSchoolid());
            ticket.setLocationid(location.getSchoolid());
//            logger.info("update ticket");
//            logger.info(String.format("%s,%s",schoolteam.getSchoolname(),location.getSchoolid()));
            ticketrepository.save(ticket);

        }

    }


}
