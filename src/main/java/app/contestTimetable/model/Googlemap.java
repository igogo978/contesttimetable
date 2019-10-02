package app.contestTimetable.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Googlemap {
    @Id
    private String id;

    private String startid;
    private String startname;

    private String endid;
    private String endname;

    private double distance;

    private Integer updatedate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartid() {
        return startid;
    }

    public void setStartid(String startid) {
        this.startid = startid;
    }

    public String getStartname() {
        return startname;
    }

    public void setStartname(String startname) {
        this.startname = startname;
    }

    public String getEndid() {
        return endid;
    }

    public void setEndid(String endid) {
        this.endid = endid;
    }

    public String getEndname() {
        return endname;
    }

    public void setEndname(String endname) {
        this.endname = endname;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Integer getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Integer updatedate) {
        this.updatedate = updatedate;
    }
}
