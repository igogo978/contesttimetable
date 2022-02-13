package app.contestTimetable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Areascore {


    @Id
    private String id;

    private String startarea;

    private String endarea;

    private double scores;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartarea() {
        return startarea;
    }

    public void setStartarea(String startarea) {
        this.startarea = startarea;
    }

    public String getEndarea() {
        return endarea;
    }

    public void setEndarea(String endarea) {
        this.endarea = endarea;
    }

    public double getScores() {
        return scores;
    }

    public void setScores(double scores) {
        this.scores = scores;
    }
}
