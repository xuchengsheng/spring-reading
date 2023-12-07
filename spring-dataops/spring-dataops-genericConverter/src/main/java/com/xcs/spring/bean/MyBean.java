package com.xcs.spring.bean;

import com.xcs.spring.annotation.DateFormat;

import java.util.Date;

public class MyBean {

    @DateFormat("yyyy-MM-dd")
    private Date date;

    @DateFormat("yyyy-MM-dd hh:mm:ss")
    private Date dateTime;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "date=" + date +
                ", dateTime=" + dateTime +
                '}';
    }
}
