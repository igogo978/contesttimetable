package app.contestTimetable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ReportScoresSummary {


    @Id
    private String uuid;

    private String scoresfrequency;

    private double scores;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
