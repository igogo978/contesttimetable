package app.contestTimetable.model.report;

import javax.persistence.*;

@Entity
public class ReportBody {

    @Id
    private String uuid;

    @OneToOne
    @MapsId
    @JoinColumn(name = "report_uuid")
    private Report report;

    @Column( columnDefinition = "text")
    private String body;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
