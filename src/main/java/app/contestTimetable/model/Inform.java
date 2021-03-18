package app.contestTimetable.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Inform {

    @Id
    private Integer id;

    @ElementCollection
    private List<String> comments = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getComments() {
        return comments;
    }

}
