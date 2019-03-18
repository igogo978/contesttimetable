package app.contestTimetable.model.school;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;


@Entity
public class SchoolTeam {

    @Id
    private String schoolid;

    private String schoolname;

    private Integer members;

    @ElementCollection
    private List<ContestItem> contestitems = new ArrayList<>();

    @ElementCollection
    private List<Contestid> contestids = new ArrayList<Contestid>();

    public SchoolTeam() {
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



    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public List<ContestItem> getContestitems() {
        return contestitems;
    }

    public List<Contestid> getContestids() {
        return contestids;
    }
}
