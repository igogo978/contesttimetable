package app.contestTimetable.model.school;


import javax.persistence.Embeddable;

@Embeddable
public class ContestItem {


    private Integer contestid;

    private String item;
    private Integer members;


    public ContestItem() {
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }


    public Integer getContestid() {
        return contestid;
    }

    public void setContestid(Integer contestid) {
        this.contestid = contestid;
    }


}
