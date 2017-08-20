package com.family.donghyunlee.family.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DONGHYUNLEE on 2017-08-15.
 */
public class TimeLineItem implements Parcelable {

    private String timeline_nickName;
    private String timeline_profileImage;
    private String timeline_date;
    private String timeline_content;
    private String timeline_userId;
    private String timeline_key;
    private String timeline_contentImage;
    private TimelineCountItem timelineCountItem;
    private int isSelect;

    TimeLineItem() {

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
        isSelect = 0;
    }
    public TimeLineItem(String timeline_key, String timeline_nickName,
                        String timeline_profileImage, String timeline_date, String timeline_content, String timeline_userId, String timeline_contentImage) {
        this.timeline_key = timeline_key;
        this.timeline_nickName = timeline_nickName;
        this.timeline_profileImage = timeline_profileImage;
        this.timeline_date = timeline_date;
        this.timeline_content = timeline_content;
        this.timeline_userId = timeline_userId;
        this.timeline_contentImage = timeline_contentImage;
        isSelect = 0;
    }

    public void setIsSelect(int isSelect) {
        this.isSelect = isSelect;
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


    public int getIsSelect() {
        return isSelect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(timeline_key);
        dest.writeString(timeline_nickName);
        dest.writeString(timeline_profileImage);
        dest.writeString(timeline_date);
        dest.writeString(timeline_content);
        dest.writeString(timeline_userId);
        dest.writeString(timeline_contentImage);
    }

    public static final Parcelable.Creator<TimeLineItem> CREATOR = new Parcelable.Creator<TimeLineItem>() {

        @Override
        public TimeLineItem createFromParcel(Parcel source) {

            String timeline_key = source.readString();
            String timeline_nickName = source.readString();
            String timeline_profileImage = source.readString();
            String timeline_date = source.readString();
            String timeline_content = source.readString();
            String timeline_userId = source.readString();
            String timeline_contentImage = source.readString();
            return new TimeLineItem( timeline_key, timeline_nickName,
                    timeline_profileImage, timeline_date, timeline_content, timeline_userId, timeline_contentImage);
            //using parcelable constructor
        }

        @Override
        public TimeLineItem[] newArray(int size) {

            return new TimeLineItem[size];
        }
    };

}
