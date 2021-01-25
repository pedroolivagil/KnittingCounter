package com.olivadevelop.knittingcounter.model;

import com.olivadevelop.knittingcounter.tools.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project {
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_CREATION_DATE = "creation_date";
    public static final String COL_LAP = "lap";
    public static final String COL_NEEDLE_NUM = "needle_num";
    public static final String COL_HEADER_IMG_URI = "header_img_uri";
    public static final String COL_OPTION_HEADER_IMG = "option_header_img";

    private Integer _id;
    private String name;
    private String creationDate;
    private Integer lap = 0;
    private Float needleNum = 0f;
    private String headerImgUri;
    private Long time = 0L;
    private List<TimerHistoric> historic = new ArrayList<>();

    private int optionHeaderImage = 0;

    public Project() {
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

    public String getCreationDate() {
        if (this.creationDate == null) {
            this.creationDate = Tools.formatDate(new Date());
        }
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getLap() {
        return lap;
    }

    public void setLap(Integer lap) {
        this.lap = lap;
    }

    public Float getNeedleNum() {
        return needleNum;
    }

    public void setNeedleNum(Float needleNum) {
        this.needleNum = needleNum;
    }

    public String getHeaderImgUri() {
        return headerImgUri;
    }

    public void setHeaderImgUri(String headerImgUri) {
        this.headerImgUri = headerImgUri;
    }

    public int getOptionHeaderImage() {
        return optionHeaderImage;
    }

    public void setOptionHeaderImage(int optionHeaderImage) {
        this.optionHeaderImage = optionHeaderImage;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<TimerHistoric> getHistoric() {
        return historic;
    }

    public void setHistoric(List<TimerHistoric> historic) {
        this.historic = historic;
    }
}
