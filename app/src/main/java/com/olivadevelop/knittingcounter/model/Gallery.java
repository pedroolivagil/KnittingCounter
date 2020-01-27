package com.olivadevelop.knittingcounter.model;

import com.olivadevelop.knittingcounter.tools.Tools;

import java.util.Date;

public class Gallery {
    public static final String COL_ID = "_id";
    public static final String COL_ID_PROJECT = "id_project";
    public static final String COL_NAME = "name";
    public static final String COL_CREATION_DATE = "creation_date";
    public static final String COL_IMG_URI = "img_uri";
    public static final String COL_OPTION_CREATE_IMG = "option_create_img";

    private Integer _id;
    private Integer id_project;
    private String name;
    private String creationDate;
    private String imgUri;
    private int optionCreateImage = 0;

    public Gallery() {
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getId_project() {
        return id_project;
    }

    public void setId_project(Integer id_project) {
        this.id_project = id_project;
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

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public int getOptionCreateImage() {
        return optionCreateImage;
    }

    public void setOptionCreateImage(int optionCreateImage) {
        this.optionCreateImage = optionCreateImage;
    }
}
