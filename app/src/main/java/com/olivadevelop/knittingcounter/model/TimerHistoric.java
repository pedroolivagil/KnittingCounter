package com.olivadevelop.knittingcounter.model;

import java.util.Date;

public class TimerHistoric {
    public String name;
    public long time;
    public Date currentDate;

    public TimerHistoric() {
    }

    public TimerHistoric(String name, long time) {
        this.name = name;
        this.time = time;
        this.currentDate = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
}
