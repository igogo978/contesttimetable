package app.contestTimetable.model.report;

import javax.persistence.*;

@Entity
public class Report {

//    https://www.baeldung.com/jpa-join-column

    @Id
    private String uuid;

    private String summary;

    private double scores;

   @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @PrimaryKeyJoinColumn
   private ReportBody reportBody;


//    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @PrimaryKeyJoinColumn
//    private ReportSerial reportSerial;


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

    public double getScores() {
        return scores;
    }

    public void setScores(double scores) {
        this.scores = scores;
    }

    public void setReportBody(ReportBody reportBody) {
        this.reportBody = reportBody;
    }

//    public void setReportSerial(ReportSerial reportSerial) {
//        this.reportSerial = reportSerial;
//    }
}
