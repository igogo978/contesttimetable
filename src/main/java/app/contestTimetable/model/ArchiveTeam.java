package app.contestTimetable.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ArchiveTeam {

    //参赛队伍, 竞赛项目区分
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer year;
    private String school;
    private String location;

     @ElementCollection
    private List<Team> teams = new ArrayList<>();

    public ArchiveTeam() {
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
