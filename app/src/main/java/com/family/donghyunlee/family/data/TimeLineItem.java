package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-15.
 */

public class TimeLineItem {

    private String timeline_nickName;
    private String timeline_profileImage;
    private String timeline_date;
    private int timeline_like_cnt;
    private int timeline_commend_cnt;
    private String timeline_content;
    private String timeline_userId;
    private String timeline_contentImage;

    TimeLineItem(){

    }
    public TimeLineItem(String timeline_nickName, String timeline_profileImage, String timeline_date, String timeline_content, int timeline_like_cnt,
                        int timeline_commend_cnt, String timeline_userId, String timeline_contentImage) {
        this.timeline_nickName = timeline_nickName;
        this.timeline_profileImage = timeline_profileImage;
        this.timeline_date = timeline_date;
        this.timeline_content = timeline_content;
        this.timeline_like_cnt = timeline_like_cnt;
        this.timeline_commend_cnt = timeline_commend_cnt;
        this.timeline_userId = timeline_userId;
        this.timeline_contentImage = timeline_contentImage;
    }


    public void setUserId(String timeline_userId) {
        this.timeline_userId = timeline_userId;
    }

    public void setProfileImagePath(String timeline_contentImage) {
        this.timeline_contentImage = timeline_contentImage;
    }

    public void setTimeline_content(String timeline_content) {
        this.timeline_content = timeline_content;
    }

    public void setTimeline_nickName(String timeline_nickName) {
        this.timeline_nickName = timeline_nickName;
    }

    public void setTimeline_profileImage(String timeline_profileImage) {
        this.timeline_profileImage = timeline_profileImage;
    }

    public void setTimeline_date(String timeline_date) {
        this.timeline_date = timeline_date;
    }

    public void setTimeline_like_cnt(int timeline_like_cnt) {
        this.timeline_like_cnt = timeline_like_cnt;
    }

    public void setTimeline_commend_cnt(int timeline_commend_cnt) {
        this.timeline_commend_cnt = timeline_commend_cnt;
    }


    public String getTimeline_content() {
        return timeline_content;
    }

    public String getTimeline_nickName() {
        return timeline_nickName;
    }

    public String getTimeline_profileImage() {
        return timeline_profileImage;
    }

    public String getTimeline_date() {
        return timeline_date;
    }

    public int getTimeline_like_cnt() {
        return timeline_like_cnt;
    }

    public int getTimeline_commend_cnt() {
        return timeline_commend_cnt;
    }

    public String getTimeline_userId() {
        return timeline_userId;
    }

    public String getTimeline_contentImage() {
        return timeline_contentImage;
    }

}
