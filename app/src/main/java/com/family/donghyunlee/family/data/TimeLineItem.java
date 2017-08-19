package com.family.donghyunlee.family.data;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * Created by DONGHYUNLEE on 2017-08-15.
 */
@Keep
public class TimeLineItem implements Serializable{

    private String timeline_nickName;
    private String timeline_profileImage;
    private String timeline_date;
    private String timeline_content;
    private String timeline_userId;
    private String timeline_key;
    private String timeline_contentImage;
    private TimelineCountItem timelineCountItem;
   TimeLineItem(){

    }

    public TimeLineItem(TimelineCountItem timelineCountItem, String timeline_key, String timeline_nickName,
                        String timeline_profileImage, String timeline_date, String timeline_content, String timeline_userId, String timeline_contentImage) {
        this.timeline_key = timeline_key;
        this.timeline_nickName = timeline_nickName;
        this.timeline_profileImage = timeline_profileImage;
        this.timeline_date = timeline_date;
        this.timeline_content = timeline_content;
        this.timeline_userId = timeline_userId;
        this.timeline_contentImage = timeline_contentImage;
        this.timelineCountItem = timelineCountItem;
    }

    public void setTimelineCountItem(TimelineCountItem timelineCountItem) {
        this.timelineCountItem = timelineCountItem;
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

    public void setTimeline_content(String timeline_content) {
        this.timeline_content = timeline_content;
    }

    public void setTimeline_userId(String timeline_userId) {
        this.timeline_userId = timeline_userId;
    }

    public void setTimeline_key(String timeline_key) {
        this.timeline_key = timeline_key;
    }

    public void setTimeline_contentImage(String timeline_contentImage) {
        this.timeline_contentImage = timeline_contentImage;
    }

    public TimelineCountItem getTimelineCountItem() {
        return timelineCountItem;
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

    public String getTimeline_content() {
        return timeline_content;
    }

    public String getTimeline_userId() {
        return timeline_userId;
    }

    public String getTimeline_key() {
        return timeline_key;
    }

    public String getTimeline_contentImage() {
        return timeline_contentImage;
    }


}
