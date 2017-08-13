package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class ToProgressItem {

    private String title;
    private String location;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String memo;
    private boolean shareCheck;
    private boolean withCheck;
    private String imgPath; // 아직

    public ToProgressItem() {

    }

    public ToProgressItem(String title, String location, String startDate, String endDate, String startTime, String endTime, String memo, boolean shareCheck, boolean withCheck) {
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.shareCheck = shareCheck;
        this.withCheck = withCheck;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getMemo() {
        return memo;
    }

    public boolean isShareCheck() {
        return shareCheck;
    }

    public boolean isWithCheck() {
        return withCheck;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setShareCheck(boolean shareCheck) {
        this.shareCheck = shareCheck;
    }

    public void setWithCheck(boolean withCheck) {
        this.withCheck = withCheck;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
