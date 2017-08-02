package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-02.
 */

public class Album {

    private String albumName;
    private String albumDate;
    private String albumCnt;

    public Album(String albumName, String albumDate, String albumCnt) {
        this.albumName = albumName;
        this.albumDate = albumDate;
        this.albumCnt = albumDate;
    }
    // GETTER
    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumDate() {
        return albumDate;
    }

    public String getAlbumCnt() {
        return albumCnt;
    }
    // SETTER
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setAlbumDate(String albumDate) {
        this.albumDate = albumDate;
    }

    public void setAlbumCnt(String albumCnt) {
        this.albumCnt = albumCnt;
    }
}
