package com.cti.repository.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {
        @Index(columnList = "user"),
        @Index(columnList = "device"),
        @Index(columnList = "type"),
        @Index(columnList = "time")},
        uniqueConstraints = @UniqueConstraint(name = "JOB_AND_DEVICE_CONSTRAINT", columnNames = {"jobId", "device"}))
public class PrintJob {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer jobId;

    private String device;
    private String user;
    private PrintType type;
    private Long time;
    private Integer amount;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public PrintType getType() {
        return type;
    }

    public void setType(PrintType type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTimeAsDate() {
        return new Date(time);
    }
}