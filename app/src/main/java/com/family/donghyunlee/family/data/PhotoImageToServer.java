package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-05.
 */

public class PhotoImageToServer {

    private int position;
    private String imgPath;
    PhotoImageToServer(){

    }

    PhotoImageToServer(String imgPath){
        this.imgPath = imgPath;
    }

    public int getPosition() {
        return position;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
