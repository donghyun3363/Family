package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-17.
 */

public class TitleItem {

    private String titleImage;
    private String title;

    public TitleItem(){

    }
    public TitleItem(String titleImage, String title) {
        this.titleImage = titleImage;
        this.title = title;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public String getTitle() {
        return title;
    }
}
