package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-27.
 */

public class PushItem {

    private String pushProfileImage;
    private String pushData;
    private String pushDate;

    PushItem(){

    }


    public PushItem(String pushProfileImage, String pushData, String pushDate) {
        this.pushProfileImage = pushProfileImage;
        this.pushData = pushData;
        this.pushDate = pushDate;
    }

    public void setPushProfileImage(String pushProfileImage) {
        this.pushProfileImage = pushProfileImage;
    }

    public void setPushData(String pushData) {
        this.pushData = pushData;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }

    public String getPushProfileImage() {
        return pushProfileImage;
    }

    public String getPushData() {
        return pushData;
    }

    public String getPushDate() {
        return pushDate;
    }
}
