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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class InformService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

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


    public List<Inform> getInformsforLocation(Boolean isPasswdVisible) {
        List<Inform> informs = new ArrayList<>();


        List<Contestconfig> configs = contestconfigRepository.findAllByOrderByIdAsc();

        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        locations.removeIf(location -> location.getLocationname().equals("未排入"));


        //4 个场次
        configs.forEach(config -> {
//            logger.info(String.valueOf(config.getId()));

            locations.forEach(location -> {
                Inform inform = new Inform();

//                inform.setContestItem(String.valueOf(config.getId() + "-" + String.join("", contestgroup)));
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
}
