package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class ToProgressItem {
    private String userId;
    private String title;
    private String location;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String memo;
    private String date;
    private String nickName;
    private boolean shareCheck;
    private String profilePath;
    private boolean complete;

    public ToProgressItem() {

    }

    public ToProgressItem(String userId, String profilePath, String nickName, String date, String title,
                          String location, String startDate, String endDate, String startTime,
                          String endTime, String memo, boolean shareCheck) {
        this.userId = userId;
        this.title = title;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.shareCheck = shareCheck;
        this.profilePath = profilePath;
        this.date = date;
        this.nickName = nickName;
        complete = false;
    }

    public boolean getComplete() {
        return complete;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getNickName() {
        return nickName;
    }

    public String getProfilePath() {
        return profilePath;
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

    public boolean getShareCheck() {
        return shareCheck;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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


    public void setDate(String date) {
        this.date = date;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
