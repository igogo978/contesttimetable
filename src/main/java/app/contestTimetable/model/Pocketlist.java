package app.contestTimetable.model;


import app.contestTimetable.model.school.Contestid;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pocketlist {


    @Id
    private String schoolid;

    private String schoolname;
    private String locationid;
    private String locationname;

    private Integer capacity;

    @ElementCollection
    private List<Contestid> Locationcontestids = new ArrayList<Contestid>();


    @ElementCollection
    private List<Contestid> Teamcontestids = new ArrayList<Contestid>();




    private String description;

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getLocationid() {
        return locationid;
    }

    public void setLocationid(String locationid) {
        this.locationid = locationid;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    public List<Contestid> getLocationcontestids() {
        return Locationcontestids;
    }

    public List<Contestid> getTeamcontestids() {
        return Teamcontestids;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
