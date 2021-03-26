package app.contestTimetable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Report {

//    https://www.baeldung.com/jpa-join-column

    @Id
    private String uuid;

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
