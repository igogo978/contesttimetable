package app.contestTimetable.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Contestconfig {

    @Id
    private Integer priority;

//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;


    @ElementCollection
    private List<String> contestgroup = new ArrayList<>();

    private String description;

    public Contestconfig() {
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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
