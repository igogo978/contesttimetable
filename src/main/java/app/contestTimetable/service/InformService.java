package app.contestTimetable.service;

import app.contestTimetable.model.Contestconfig;
import app.contestTimetable.model.Team;
import app.contestTimetable.model.pocketlist.Inform;
import app.contestTimetable.model.school.Location;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.PocketlistRepository;
import app.contestTimetable.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class InformService {
    Logger logger = LoggerFactory.getLogger(InformService.class);

    @Autowired
    ContestconfigRepository contestconfigRepository;

    @Autowired
    PocketlistService pocketlistService;

    @Autowired
    PocketlistRepository pocketlistRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    XlsxService xlsxService;


    public List<Inform> getInformsByLocation(Boolean isPasswdVisible) {
        List<Inform> informs = new ArrayList<>();


        List<Contestconfig> configs = contestconfigRepository.findAllByOrderByIdAsc();

        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        locations.removeIf(location -> location.getLocationname().equals("未排入"));


        //4 个场次
        configs.forEach(config -> {

            locations.forEach(location -> {
                Inform inform = new Inform();

                inform.setContestItem(String.valueOf(config.getId()));
                inform.setTeamsize(0);
                inform.setLocation(location.getLocationname());
                inform.setDescription(config.getDescription());
                AtomicReference<Integer> teamsize = new AtomicReference<>(0);
                AtomicReference<Integer> totalpeople = new AtomicReference<>(0);

                config.getContestgroup().forEach(contestitem -> {


                    List<Team> teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationname(), contestitem.toUpperCase());
                    teams.forEach(team -> {
                        if (team.getMembername() != null) {
//                            logger.info(String.format("%s,%s", team.getUsername(), team.getMembername()));
                            totalpeople.updateAndGet(v -> v + 2);
                        } else {
                            totalpeople.updateAndGet(v -> v + 1);
                        }
                    });

                    teams.forEach(team -> {
                        team.setDescription(team.getDescription().substring(2));
                        if (!isPasswdVisible) {
                            team.setAccount("*****");
                            team.setPasswd("*****");
                        }

                        inform.getTeams().add(team);
                    });
                    teamsize.updateAndGet(v -> v + teams.size());
                });
                inform.setTeamsize(teamsize.get());
                inform.setTotalPeople(totalpeople.get());
                informs.add(inform);

            });

        });


        return informs;
    }

    public List<Inform> getInformsAll(Boolean isLogin) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //download pdf
        List<Contestconfig> configs = contestconfigRepository.findAllByOrderByIdAsc();
        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        locations.removeIf(location -> location.getLocationname().equals("未排入"));

        String filename = "inform-all.pdf";

        List<Inform> informs = new ArrayList<>();

        HashMap<Inform, List<Team>> informAll = new HashMap<>();

        //3 个场次
        configs.forEach(config -> {

            List<String> contestgroup = config.getContestgroup().stream().map(item -> item.toUpperCase() + "組").collect(Collectors.toList());

            locations.forEach(location -> {
                Inform inform = new Inform();

                inform.setContestItem(String.join("、", contestgroup));
                inform.setTeamsize(-1);
                inform.setLocation(location.getLocationname());
                inform.setDescription(config.getDescription());
                AtomicReference<Integer> teamsize = new AtomicReference<>(-1);
                AtomicReference<Integer> totalpeople = new AtomicReference<>(-1);

                config.getContestgroup().forEach(contestitem -> {

                    List<Team> teams = teamRepository.findByLocationAndContestitemContaining(location.getLocationname(), contestitem.toUpperCase());
                    teams.forEach(team -> {
                        if (team.getMembername() != null) {
//                            logger.info(String.format("%s,%s", team.getUsername(), team.getMembername()));
                            totalpeople.updateAndGet(v -> v + 1);
                        } else {
                            totalpeople.updateAndGet(v -> v + 0);
                        }
                    });
//                    inform.getTeams().addAll(teams);
                    teams.forEach(team -> {
                        team.setDescription(team.getDescription().substring(1));
                        if (isLogin == Boolean.FALSE) {
                            team.setAccount("*****");
                            team.setPasswd("*****");
                        }

                        inform.getTeams().add(team);
                    });
                    teamsize.updateAndGet(v -> v + teams.size());
                });
                inform.setTeamsize(teamsize.get());
                inform.setTotalPeople(totalpeople.get());
                informs.add(inform);

            });

        });


        return informs;
    }



}
