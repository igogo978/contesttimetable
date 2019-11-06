package app.contestTimetable.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Contestconfig {

    @Id
    private Integer id;

//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;


    @ElementCollection
    private List<String> contestgroup = new ArrayList<>();

    private String description;

//    private Integer count;

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

//    public Integer getCount() {
//        return count;
//    }
//
//    public void setCount(Integer count) {
//        this.count = count;
//    }
}
