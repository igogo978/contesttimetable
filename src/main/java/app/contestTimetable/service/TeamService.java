package app.contestTimetable.service;


import app.contestTimetable.model.Team;
import app.contestTimetable.repository.ContestconfigRepository;
import app.contestTimetable.repository.TeamRepository;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    XlsxService readxlsx;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SchoolTeamService schoolTeamService;


    @Autowired
    ContestconfigRepository contestconfigRepository;


    public void updateTeam(String zipFilePath) throws IOException {

        //1. upzip file to /tmp/team
        String dstDirPath = "/tmp/team";

        File dstDir = new File(dstDirPath);

        // create output directory if it doesn't exist
        if (!dstDir.exists()) {
            logger.info("zip file, mkdir /tmp/team ");
            dstDir.mkdirs();
        }

        //clean files in this directory
        Arrays.stream(new File(dstDirPath).listFiles()).forEach(File::delete);



        try (ZipArchiveInputStream inputStream = getZipFile(new File(zipFilePath))) {

            ZipArchiveEntry entry = null;
            while ((entry = inputStream.getNextZipEntry()) != null) {

                OutputStream os = null;
                try {
//                    logger.info(entry.getName());
//                    String filename = String.format("%s.%s", System.currentTimeMillis(), "xlsx");
                    os = new BufferedOutputStream(new FileOutputStream(new File(dstDir, entry.getName())));
                    //输出文件路径信息
                    IOUtils.copy(inputStream, os);
                } finally {
                    IOUtils.closeQuietly(os);
                }

            }


        //2. read data from xlsx
            //读取参赛队伍
            List<Team> teams = new ArrayList<>();
            teams = readxlsx.getTeams(dstDirPath);

            teams.forEach(team -> {
                teamRepository.save(team);
            });

            //update contesttime in team.description
            contestconfigRepository.findAll().forEach(contestconfig -> {
                contestconfig.getContestgroup().forEach(contestgroup -> {
                            teamRepository.findByContestitemContaining(contestgroup).forEach(team -> {
                                team.setDescription(contestconfig.getDescription());
                            });
                        }
                );
            });

            //统计每间学校各项目参赛人数
            logger.info("统计每间学校各项目参赛人数");
            schoolTeamService.updateSchoolTeam();



        } catch (Exception e) {
            logger.error("[unzip] 解压zip文件出错", e);
        }

    }

    private static ZipArchiveInputStream getZipFile(File zipFile) throws Exception {
        return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
    }

}
