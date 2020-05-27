package app.contestTimetable.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ExtHanzi {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    private String characters;

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }
}
