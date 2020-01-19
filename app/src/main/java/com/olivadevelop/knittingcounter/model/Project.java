package com.olivadevelop.knittingcounter.model;

public class Project {

    private Integer _id;
    private String name;
    private String creation_date;
    private Integer lap;

    public Project() {
    }

    public Project(Integer _id, String name, String creation_date, Integer lap) {
        this._id = _id;
        this.name = name;
        this.creation_date = creation_date;
        this.lap = lap;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public Integer getLap() {
        return lap;
    }

    public void setLap(Integer lap) {
        this.lap = lap;
    }
}
