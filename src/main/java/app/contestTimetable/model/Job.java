package app.contestTimetable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;


public class Job {

//    private List<Integer> jobid;

    private String locationorder;

    private String priorityorder;

    private String group1order;


    private String group2order;

    private Integer prioritysize;
    private Integer group1size;
    private Integer group2size;


    public Job() {
    }

//    public List<Integer> getJobid() {
//        return jobid;
//    }
//
//    public void setJobid(List<Integer> jobid) {
//        this.jobid = jobid;
//    }

    public String getLocationorder() {
        return locationorder;
    }

    public void setLocationorder(String locationorder) {
        this.locationorder = locationorder;
    }

    public String getGroup1order() {
        return group1order;
    }

    public void setGroup1order(String group1order) {
        this.group1order = group1order;
    }

    public String getGroup2order() {
        return group2order;
    }

    public void setGroup2order(String group2order) {
        this.group2order = group2order;
    }

    public String getPriorityorder() {
        return priorityorder;
    }

    public void setPriorityorder(String priorityorder) {
        this.priorityorder = priorityorder;
    }

    public Integer getPrioritysize() {
        return prioritysize;
    }

    public void setPrioritysize(Integer prioritysize) {
        this.prioritysize = prioritysize;
    }

    public Integer getGroup1size() {
        return group1size;
    }

    public void setGroup1size(Integer group1size) {
        this.group1size = group1size;
    }

    public Integer getGroup2size() {
        return group2size;
    }

    public void setGroup2size(Integer group2size) {
        this.group2size = group2size;
    }


}
