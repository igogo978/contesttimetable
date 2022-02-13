package app.contestTimetable.service;


import app.contestTimetable.model.pocketlist.LocationSummary;
import app.contestTimetable.model.report.Report;
import app.contestTimetable.model.report.ReportBody;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Pocketlist;
import app.contestTimetable.model.school.Contestid;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PocketlistService {


    @Autowired
    PocketlistRepository pocketlistRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamService teamService;

    @Autowired
    ContestconfigRepository contestconfigRepository;


    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ReportBodyRepository reportBodyRepository;


    Logger logger = LoggerFactory.getLogger(this.getClass());
    ObjectMapper mapper = new ObjectMapper();


//    @GetMapping(value = "/api/pocketlist")
    public Map<String, List<LocationSummary>> getSummary() throws IOException {

        String str = new String("打字");
        Map<String, List<LocationSummary>> locationMp = new HashMap<>();

        List<Location> locations = locationRepository.findBySchoolidNotIn(Arrays.asList("999999"));

        locations.forEach(location -> {
            List<LocationSummary> lists = new ArrayList<>();

//            AtomicInteger contestid = new AtomicInteger(1);

            contestconfigRepository.findAllByOrderByIdAsc().forEach(contestconfig -> {
                LocationSummary locationSummary = new LocationSummary();
//                locationSum.setLocation(location.getLocationname());

                AtomicInteger contestidMembers = new AtomicInteger();
                AtomicInteger contestidUsbFlashCount = new AtomicInteger();
                contestidMembers.set(0);
                contestidUsbFlashCount.set(0);
                contestconfig.getContestgroup().forEach(contestitem -> {


                    List<Team> teams = new ArrayList<>();
                    AtomicInteger contestitemMembers = new AtomicInteger(0);

                    teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationName(), contestitem);
                    teams.forEach(team -> {
                        contestitemMembers.updateAndGet(n -> n + team.getMembers());

                        //for usb count
                        if (!contestitem.matches("(.*)"+str+"(.*)")) {
                            contestidUsbFlashCount.updateAndGet(n->n+team.getMembers());
                        }

                    });



                    locationSummary.getContestitem().put(contestitem, contestitemMembers.get());

                    contestidMembers.updateAndGet(n -> n + contestitemMembers.get());

                });
                locationSummary.setContestid(contestconfig.getId());
                locationSummary.setMembers(contestidMembers.get());
                locationSummary.setUsbFlashDriveNumbers(contestidUsbFlashCount.get());
//                contestid.incrementAndGet();
                lists.add(locationSummary);


            });

            List<LocationSummary> items = new ArrayList<>();
            locationMp.computeIfAbsent(location.getLocationName(), k -> items);
            lists.forEach(list -> items.add(list));

        });

        ObjectMapper mapper = new ObjectMapper();
        return locationMp;
    }

    public void restorePocketlist(String pocketlistfile) throws IOException {

        logger.info("pocketlist file:" + pocketlistfile);
        ObjectMapper mapper = new ObjectMapper();
        if (new File(pocketlistfile).exists()) {
            Pocketlist[] lists = mapper.readValue(new File(pocketlistfile), Pocketlist[].class);
            Arrays.stream(lists).forEach(pocketlistitem -> {
                logger.info(String.format("%s,%s", pocketlistitem.getLocationname(), pocketlistitem.getSchoolname()));
                pocketlistRepository.save(pocketlistitem);

                //            更新team的location
                List<Team> teams = teamRepository.findBySchoolname(pocketlistitem.getSchoolname());
                if (teams.size() != 0) {
                    teams.forEach(team -> {
                        logger.info("update teams' location");

                        team.setLocation(pocketlistitem.getLocationname());
                        teamRepository.save(team);
                    });
                }


            });


        }


    }

    public void updatePocketlist(String payload) throws IOException {

        String uuid = "1";
        Report report = new Report();
        report.setUuid(uuid);
//        report.setReport(payload);
        report.setScores(1.0);
//        reportRepository.save(report);

        ReportBody reportBody = new ReportBody();
        reportBody.setReport(report);
        reportBody.setBody(payload);
        logger.info("delete rport uuid 1");
        // Primary key can't be modified. it should be deleted then added.
        if (reportRepository.findByUuid(uuid).isPresent()) {
            reportRepository.deleteById(uuid);
        }
        reportBodyRepository.save(reportBody);
//
//        ReportScoresSummary reportScoresSummary = new ReportScoresSummary();
//        reportScoresSummary.setUuid("1");
//        reportScoresSummary.setScores(1.0);
//        reportScoresSummaryRepository.save(reportScoresSummary);

        ObjectMapper mapper = new ObjectMapper();

//        logger.info(payload);

        JsonNode items = mapper.readTree(payload);
        pocketlistRepository.deleteAll();

        items.forEach(node -> {
            JsonNode location = node.get("location");
            JsonNode item = node.get("teams");
            item.forEach(team -> {

                Pocketlist pocketlist = new Pocketlist();

                //location
                pocketlist.setLocationid(location.get("schoolid").asText());
                pocketlist.setLocationname(location.get("name").asText());
                pocketlist.setCapacity(location.get("capacity").asInt());

                location.get("contestids").forEach(contest -> {
//                    logger.info(contest.get("contestid").asText());
                    Contestid contestid = new Contestid();
                    contestid.setContestid(contest.get("contestid").asInt());
                    contestid.setMembers(contest.get("members").asInt());
                    pocketlist.getLocationcontestids().add(contestid);
                });


                pocketlist.setSchoolid(team.get("schoolid").asText());
                pocketlist.setSchoolname(team.get("name").asText());
//                logger.info(team.get("name").asText()+":"+team.get("scores").asInt());
                pocketlist.setDescription(team.get("scores").asText());
                JsonNode contestidsNode = team.get("contestids");
                contestidsNode.forEach(contest -> {
                    Contestid contestid = new Contestid();
                    contestid.setContestid(contest.get("contestid").asInt());
                    contestid.setMembers(contest.get("members").asInt());
                    pocketlist.getTeamcontestids().add(contestid);

                });
                pocketlistRepository.save(pocketlist);

            });
        });

        //            更新team的location 1015
        logger.info("update teams' location");

        //empty records
        teamRepository.findAllByOrderByLocation().forEach(team -> {
            team.setLocation("");
            teamRepository.save(team);
        });

        List<Pocketlist> pocketlist = new ArrayList<>();
        pocketlistRepository.findAll().forEach(pocketlist::add);

        pocketlist.forEach(pocketitem -> {
//                logger.info("学校：" + pocketitem.getSchoolname() + "in" + pocketitem.getLocationname());

            List<Team> teams = teamRepository.findBySchoolname(pocketitem.getSchoolname());
            if (teams.size() != 0) {
                teams.forEach(team -> {
                    team.setLocation(pocketitem.getLocationname());

                    Location location = locationRepository.findByLocationName(pocketitem.getLocationname());
                    List<Map<String, String>> comments = new ArrayList<>();
                    Map<String, String> comment = new HashMap<>();
                    comment.put("color", location.getColor());
                    comments.add(comment);
                    try {
                        team.setComments(mapper.writeValueAsString(comments));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    teamRepository.save(team);
                });
            }

        });


    }

    public List<Team> getPocketlistByPlayer() {
//        List<Team> teams = new ArrayList<>();
//        teamRepository.findAll(Sort.by("location").and(Sort.by("schoolname")).and(Sort.by("description").and(Sort.by("contestitem")))).forEach(team -> {
//            team.setAccount("");
//            team.setPasswd("");
////            team.setDescription(team.getDescription().split("-")[1]);
//            team.setDescription(team.getDescription().substring(2));
//            teams.add(team);
//        });

        return teamService.getTeamsByPlayer();
    }


    public List<Team> getPocketlistByLocation() {
//        List<Team> teams = new ArrayList<>();
//        teamRepository.findAll(Sort.by("location").and(Sort.by("description")).and(Sort.by("contestitem").and(Sort.by("schoolname")))).forEach(team -> {
//                    team.setAccount("");
//                    team.setPasswd("");
//                    teams.add(team);
//        });


        return teamService.getTeamsByLocation();
    }

    public void delete(){
        pocketlistRepository.deleteAll();
        teamRepository.findAllByOrderByLocation().forEach(team -> {
            team.setLocation("");
            teamRepository.save(team);
        });
    }

}
