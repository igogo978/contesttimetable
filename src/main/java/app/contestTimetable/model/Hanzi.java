package app.contestTimetable.model;

import javax.persistence.*;

@Entity
public class Hanzi {

    @Id
    private String id;

    private String hanzi;

    private String font;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getHanzi() {
        return hanzi;
    }

    public void setHanzi(String hanzi) {
        this.hanzi = hanzi;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
