package app.contestTimetable.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Ticket {

    @Id
    private String schoolid;

    private String location;

    public Ticket() {
    }


    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
