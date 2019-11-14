package app.contestTimetable.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class School {

    @Id
    private String schoolid;

    private String schoolname;


//    private String position;
//    private String xyposition;

    public School() {
    }

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


}
