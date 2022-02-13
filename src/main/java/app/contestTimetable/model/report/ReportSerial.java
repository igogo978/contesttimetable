//package app.contestTimetable.model.report;
//
//import javax.persistence.*;
//
//@Entity
//public class ReportSerial {
//
//    @Id
//    private String uuid;
//
//    @OneToOne
//    @MapsId
//    @JoinColumn(name = "report_uuid")
//    private Report report;
//
//    @Column( columnDefinition = "text")
//    private String serial;
//
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }
//
//    public String getSerial() {
//        return serial;
//    }
//
//    public void setSerial(String serial) {
//        this.serial = serial;
//    }
//
//    public Report getReport() {
//        return report;
//    }
//
//    public void setReport(Report report) {
//        this.report = report;
//    }
//}
