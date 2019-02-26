package app.contestTimetable.storage;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("storage")
public class StorageProperties {

    //    private String location = "upload-dir";
    @Value("${multipart.location}")
    private String location;
//    private String location = System.getProperty("java.io.tmpdir");

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
