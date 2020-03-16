package app.contestTimetable.model.pocketlist;

import app.contestTimetable.model.Team;

import java.util.ArrayList;
import java.util.List;

public class Inform {
    private String location;
    private String contestItem;
    private String description;
    private Integer totalPeople;
    private Integer teamsize;
    private List<Team> teams = new ArrayList<>();

    //totalPeopl 含简报组别,可能有两人
    //teams, 一队一张帐号密码
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContestItem() {
        return contestItem;
    }

    public void setContestItem(String contestItem) {
        this.contestItem = contestItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(Integer totalPeople) {
        this.totalPeople = totalPeople;
    }

    public Integer getTeamsize() {
        return teamsize;
    }

    public void setTeamsize(Integer teamsize) {
        this.teamsize = teamsize;
    }

    public List<Team> getTeams() {
        return teams;
    }



}
