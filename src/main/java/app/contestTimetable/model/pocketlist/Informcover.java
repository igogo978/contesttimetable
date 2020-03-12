package app.contestTimetable.model.pocketlist;

public class Informcover {
    private String location;
    private String contestItem;
    private String description;
    private Integer totalPeople;
    private Integer teams;

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

    public Integer getTeams() {
        return teams;
    }

    public void setTeams(Integer teams) {
        this.teams = teams;
    }
}
