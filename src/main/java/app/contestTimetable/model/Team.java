package app.contestTimetable.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Team {

    //参赛队伍, 竞赛项目区分

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String contestgroup;
    private String schoolname;
    private String username;

    private String member;
    private String instructor;


    public Team() {
    }

    public String getContestgroup() {
        return contestgroup;
    }

    public void setContestgroup(String contestgroup) {
        this.contestgroup = contestgroup;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
}
