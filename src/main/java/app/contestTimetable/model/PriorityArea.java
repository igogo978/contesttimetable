package app.contestTimetable.model;


import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PriorityArea {

    @Id
    String location;

    @ElementCollection
    List<String> areas = new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getAreas() {
        return areas;
    }
}