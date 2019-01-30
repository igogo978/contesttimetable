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


}
