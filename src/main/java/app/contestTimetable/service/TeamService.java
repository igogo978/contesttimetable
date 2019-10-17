package app.contestTimetable.service;


import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(this.getClass());


    public void updateTeam(String zipFilePath) throws IOException {

        //upzip file to /tmp/team/
        String dstDirPath = "/tmp/";

        File dstDir = new File(dstDirPath);

        // create output directory if it doesn't exist
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        }

        //clean files in this directory
//        Arrays.stream(new File(dstDirPath).listFiles()).forEach(File::delete);

        try (ZipArchiveInputStream inputStream = getZipFile(new File(zipFilePath))) {

            ZipArchiveEntry entry = null;
            while ((entry = inputStream.getNextZipEntry()) != null) {
                if (entry.isDirectory()) {
                    File directory = new File(dstDir, entry.getName());
                    directory.mkdirs();
                } else {
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(dstDir, entry.getName())));
                        //输出文件路径信息
                        logger.info("解压文件的当前路径为:{}", dstDir + entry.getName());
                        IOUtils.copy(inputStream, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }


        } catch (Exception e) {
            logger.error("[unzip] 解压zip文件出错", e);
        }

    }

    private static ZipArchiveInputStream getZipFile(File zipFile) throws Exception {
        return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
    }

}
