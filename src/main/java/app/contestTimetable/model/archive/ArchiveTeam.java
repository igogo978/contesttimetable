package app.contestTimetable.model.archive;

import javax.persistence.*;


@Embeddable
public class ArchiveTeam {

    private String contestitem;
    private String username;

    private String membername;
    private String instructor;

    private String description;


    private Integer members = 1;

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public ArchiveTeam() {
    }

    public String getContestitem() {
        return contestitem;
    }

    public void setContestitem(String contestitem) {
        this.contestitem = contestitem;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
