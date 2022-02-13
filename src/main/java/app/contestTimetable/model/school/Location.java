package app.contestTimetable.model.school;

import app.contestTimetable.model.school.Contestid;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Location {

    @Id
    private String schoolid;
    private String locationName;
    private int capacity;

    @ElementCollection
    private List<Contestid> contestids = new ArrayList<Contestid>();

    private String color;

    public Location() {
    }

    public Location(String schoolid, String locationName, int capacity) {
        this.schoolid = schoolid;
        this.locationName = locationName;
        this.capacity = capacity;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Contestid> getContestids() {
        return contestids;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
