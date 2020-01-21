package com.olivadevelop.knittingcounter.model;

public class Project {

    private Integer _id;
    private String name;
    private String creation_date;
    private Integer lap;
    private Integer needle_num;
    private String header_img_uri;

    public Project() {
    }

    public void addLap() {
        this.lap = lap + 1;
        if (this.lap > 9999) {
            this.lap = 9999;
        }
    }

    public void removeLap() {
        this.lap = lap - 1;
        if (this.lap < 0) {
            this.lap = 0;
        }
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

    public Integer getNeedle_num() {
        return needle_num;
    }

    public void setNeedle_num(Integer needle_num) {
        this.needle_num = needle_num;
    }

    public String getHeader_img_uri() {
        return header_img_uri;
    }

    public void setHeader_img_uri(String header_img_uri) {
        this.header_img_uri = header_img_uri;
    }
}
