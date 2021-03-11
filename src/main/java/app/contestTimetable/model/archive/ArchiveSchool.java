package app.contestTimetable.model.archive;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ArchiveSchool {

    //参赛队伍, 竞赛项目区分
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer year;
    private String school;
    private String location;
    private Integer members;
    private String description;


    @ElementCollection
    private final List<ArchiveTeam> teams = new ArrayList<>();

    public ArchiveSchool() {
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

    public List<ArchiveTeam> getTeams() {
        return teams;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
