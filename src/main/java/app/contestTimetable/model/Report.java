package app.contestTimetable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Report {


    @Id
    private String uuid;


    private Integer contestid;

    private String scoresfrequency;


    @Column( columnDefinition = "text")
    private String report;

    private double scores;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getContestid() {
        return contestid;
    }

    public void setContestid(Integer contestid) {
        this.contestid = contestid;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public double getScores() {
        return scores;
    }

    public void setScores(double scores) {
        this.scores = scores;
    }

    public String getScoresfrequency() {
        return scoresfrequency;
    }

    public void setScoresfrequency(String scoresfrequency) {
        this.scoresfrequency = scoresfrequency;
    }
}
