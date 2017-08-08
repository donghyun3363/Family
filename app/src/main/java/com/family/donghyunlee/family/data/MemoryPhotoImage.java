package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-06.
 */

public class MemoryPhotoImage {


    private String id;
    private String albumDate;
    private String imgPath;

    MemoryPhotoImage(){

    }

    public MemoryPhotoImage(String id, String albumDate, String imgPath) {
        this.id = id;
        this.albumDate = albumDate;
        this.imgPath = imgPath;
    }

    public String getId() {
        return id;
    }

    public String getAlbumDate() {
        return albumDate;
    }

    public String getImgPath() {
        return imgPath;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setAlbumDate(String albumDate) {
        this.albumDate = albumDate;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
