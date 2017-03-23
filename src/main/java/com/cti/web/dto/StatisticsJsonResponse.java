package com.cti.web.dto;


import com.cti.repository.model.PrintType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class StatisticsJsonResponse {

    private Integer jobId;
    private String device;
    private String user;
    private PrintType type;
    private Integer amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private Date time;

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
