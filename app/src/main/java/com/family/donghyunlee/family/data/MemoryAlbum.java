package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-02.
 */

public class MemoryAlbum {

    private String id;
    private String albumTitle;
    private String albumDate;
    private String mainImgPath;
    private String profileImgPath;
    private String userId;

    MemoryAlbum() {
    }

    public MemoryAlbum(String id,  String userId, String albumTitle, String albumDate, String mainImgPath, String profileImgPath) {
        this.id = id;
        this.userId = userId;
        this.albumTitle = albumTitle;
        this.albumDate = albumDate;
        this.mainImgPath = mainImgPath;
        this.profileImgPath = profileImgPath;
    }

    // GETTER


    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumDate() {
        return albumDate;
    }

    public String getMainImgPath() {
        return mainImgPath;
    }

    public String getProfileImgPath() {
        return profileImgPath;
    }
    // SETTER


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public void setAlbumDate(String albumDate) {
        this.albumDate = albumDate;
    }

    public void setMainImgPath(String mainImgPath) {
        this.mainImgPath = mainImgPath;
    }

    public void setProfileImgPath(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }
}
