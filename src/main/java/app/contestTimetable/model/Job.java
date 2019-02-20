package app.contestTimetable.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Job {



    @Id
    private String jobid;


    @Column(length = 1000)
    private String locationorder;

    @Column(length = 10000)
    private String group1order;

    @Column(length = 10000)
    private String group2order;

    private Integer group1count;
    private Integer group2count;


    private long calculatejob;

    public Job() {
    }


    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

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

    public Integer getGroup1count() {
        return group1count;
    }

    public void setGroup1count(Integer group1count) {
        this.group1count = group1count;
    }

    public Integer getGroup2count() {
        return group2count;
    }

    public void setGroup2count(Integer group2count) {
        this.group2count = group2count;
    }

    public long getCalculatejob() {
        return calculatejob;
    }

    public void setCalculatejob(long calculatejob) {
        this.calculatejob = calculatejob;
    }
}
