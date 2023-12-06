package com.xcs.spring.bean;

import java.nio.file.Path;
import java.util.Date;

public class MyBean {

    private Path path;

    private Date date;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "path=" + path +
                ", date=" + date +
                '}';
    }
}
