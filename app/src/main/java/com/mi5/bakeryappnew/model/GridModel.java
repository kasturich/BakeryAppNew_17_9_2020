package com.mi5.bakeryappnew.model;

/**
 * Created by User on 22-08-2018.
 */

public class GridModel {

    private String mainTitle;
    private int subTitle;
    private int thumbnail;
    private int colorCode;
    private int circleColorCode;

    public GridModel() {
    }

    public GridModel(String mainTitle, int subTitle, int thumbnail, int colorCode, int circleColorCode) {
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.thumbnail = thumbnail;
        this.colorCode = colorCode;
        this.circleColorCode = circleColorCode;
    }

    public int getCircleColorCode() {
        return circleColorCode;
    }

    public void setCircleColorCode(int circleColorCode) {
        this.circleColorCode = circleColorCode;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public int getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(int subTitle) {
        this.subTitle = subTitle;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
