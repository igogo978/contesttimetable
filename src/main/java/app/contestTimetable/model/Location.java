package app.contestTimetable.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Location {

    @Id
    private String schoolid;

    private String locationname;

    private int capacity;

    public Location() {
    }

    public Location(String schoolid, String locationname, int capacity) {
        this.schoolid = schoolid;
        this.locationname = locationname;
        this.capacity = capacity;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
