package app.contestTimetable.model.report;

import javax.persistence.*;

@Entity
public class Report {

//    https://www.baeldung.com/jpa-join-column

    @Id
    private String uuid;

    private String summary;

    @Column( columnDefinition = "text")
    private String serial;

    private double scores;


   @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @PrimaryKeyJoinColumn
   private ReportBody reportBody;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public double getScores() {
        return scores;
    }

    public void setScores(double scores) {
        this.scores = scores;
    }

    public void setReportBody(ReportBody reportBody) {
        this.reportBody = reportBody;
    }
}
