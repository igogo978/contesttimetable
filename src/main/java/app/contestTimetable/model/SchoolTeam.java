package app.contestTimetable.model;

public class SchoolTeam {

    private String schoolid;
    private String schoolname;

    private String contestgroup;
    private Integer members;


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

    public String getContestgroup() {
        return contestgroup;
    }

    public void setContestgroup(String contestgroup) {
        this.contestgroup = contestgroup;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }
}
