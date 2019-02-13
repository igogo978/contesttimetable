package app.contestTimetable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Report {


    @Id
    private String uuid;

    private  String contestpriority;


    @Column( columnDefinition = "text")
    private String report;

    private double distance;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContestpriority() {
        return contestpriority;
    }

    public void setContestpriority(String contestpriority) {
        this.contestpriority = contestpriority;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
