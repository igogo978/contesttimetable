package app.contestTimetable.service;


import app.contestTimetable.model.Team;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.LocationRepository;
import app.contestTimetable.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    XlsxService readxlsx;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SchoolTeamService schoolTeamService;


    @Autowired
    ContestconfigRepository contestconfigRepository;

    @Autowired
    LocationRepository locationRepository;

    @Value("${multipart.location}")
    private Path path;

    public List<Team> findTeamByLocationAndContestitemNotContain(String location, String contestitemnotcontain) {
        List<Team> teams = new ArrayList<>();

        teams = teamRepository.findByLocationAndContestitemNotContainingOrderByLocation(location, contestitemnotcontain);
        return teams;
    }

    public void updateTeam() throws IOException {
        List<Team> teams = new ArrayList<>();
        teams = readxlsx.getTeams(path);

        //empty old records
        teamRepository.deleteAll();

        teams.forEach(team -> {
            teamRepository.save(team);
        });

        //update contest data in team.description
        contestconfigRepository.findAll().forEach(contestconfig -> {
            contestconfig.getContestgroup().forEach(contestitem -> {

                        List<Team> contestitemTeams = teamRepository.findByContestitemContaining(contestitem.toUpperCase());

                        if (contestitemTeams.size() == 0) {
                            logger.info(contestitem);
                        }
                        contestitemTeams.forEach(team -> {
                            team.setDescription(contestconfig.getId() + "-" + contestconfig.getDescription());
                        });
                    }
            );
        });

    }

    public void update() throws IOException {
        updateTeam();
        //统计每间学校各项目参赛人数
        logger.info("统计每间学校各项目参赛人数");
        schoolTeamService.updateSchoolTeam();
    }

//    public void updateTeam(String zipFilePath) throws IOException {
//        //1. upzip file to /tmp/team
//        String dstDirPath = "/tmp/team";
//        File dstDir = new File(dstDirPath);
//
//        //empty dstDirPath
//        dstDir.delete();
//
//
//        // create output directory if it doesn't exist
//        if (!dstDir.exists()) {
//            logger.info("zip file, mkdir /tmp/team ");
//            dstDir.mkdirs();
//        }
//
//        //clean files in this directory
//        Arrays.stream(new File(dstDirPath).listFiles()).forEach(File::delete);
//
//
//        try (ZipArchiveInputStream inputStream = getZipFile(new File(zipFilePath))) {
//
//            ZipArchiveEntry entry = null;
//            while ((entry = inputStream.getNextZipEntry()) != null) {
//
//                OutputStream os = null;
//                try {
////                    logger.info(entry.getName());
////                    String filename = String.format("%s.%s", System.currentTimeMillis(), "xlsx");
//                    os = new BufferedOutputStream(new FileOutputStream(new File(dstDir, entry.getName())));
//                    //输出文件路径信息
//                    IOUtils.copy(inputStream, os);
//                } finally {
//                    IOUtils.closeQuietly(os);
//                }
//
//            }
//
//
//            //2. read data from xlsx
//            //读取参赛队伍
//            List<Team> teams = new ArrayList<>();
//            teams = readxlsx.getTeams(path);
//
//            //empty old records
//            teamRepository.deleteAll();
//
//            teams.forEach(team -> {
//                teamRepository.save(team);
//            });
//
//            //update contesttime in team.description
//            contestconfigRepository.findAll().forEach(contestconfig -> {
//                contestconfig.getContestgroup().forEach(contestitem -> {
//
//                            List<Team> contestitemTeams = teamRepository.findByContestitemContaining(contestitem.toUpperCase());
//
//                            if (contestitemTeams.size() == 0) {
//                                logger.info(contestitem);
//                            }
//                            contestitemTeams.forEach(team -> {
//                                team.setDescription(contestconfig.getId() + "-" + contestconfig.getDescription());
//                            });
//                        }
//                );
//            });
//
//            //统计每间学校各项目参赛人数
//            logger.info("统计每间学校各项目参赛人数");
//            schoolTeamService.updateSchoolTeam();
//
//
//        } catch (Exception e) {
//            logger.error("read文件出错", e);
//        }
//
//    }


}
