package app.contestTimetable.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Contestconfig {

    @Id
    private Integer id;

    @ElementCollection
    private List<String> contestgroup = new ArrayList<>();

    private String description;

    public Contestconfig() {
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getContestgroup() {
        return contestgroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
