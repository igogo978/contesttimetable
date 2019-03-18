package app.contestTimetable.model.school;


import javax.persistence.Embeddable;

@Embeddable
public class Contestid {

    private Integer contestid;
    private Integer members;


    public Contestid() {
    }

    public Integer getContestid() {
        return contestid;
    }

    public void setContestid(Integer contestid) {
        this.contestid = contestid;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }
}
