package com.family.donghyunlee.family.photoalbum;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
 */

public class MemoryAlbumItem {


    private String title;
    private String filePath;
    private String albumNum;


    MemoryAlbumItem(String title, String filepath, String albumNum) {
        this.title = title;
        this.filePath = filepath;
        this.albumNum = albumNum;

    }
    MemoryAlbumItem(String title, String albumNum) {
        this.title = title;
        this.albumNum = albumNum;

    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getAlbumNum() {
        return albumNum;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setAlbumNum(String albumNum) {
        this.albumNum = albumNum;
    }
}
