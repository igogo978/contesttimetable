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

    private Integer group1numbers;
    private Integer group2numbers;


    private long calculatejobs;

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

    public Integer getGroup1numbers() {
        return group1numbers;
    }

    public void setGroup1numbers(Integer group1numbers) {
        this.group1numbers = group1numbers;
    }

    public Integer getGroup2numbers() {
        return group2numbers;
    }

    public void setGroup2numbers(Integer group2numbers) {
        this.group2numbers = group2numbers;
    }

    public long getCalculatejobs() {
        return calculatejobs;
    }

    public void setCalculatejobs(long calculatejobs) {
        this.calculatejobs = calculatejobs;
    }
}
