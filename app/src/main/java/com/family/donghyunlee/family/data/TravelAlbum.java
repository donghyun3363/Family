package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-05.
 */

public class TravelAlbum {

    private String id;
    private String albumTitle;
    private String albumDate;
    private String mainImgPath;
    private String profileImgPath;
    private String albumLocation;

    TravelAlbum() {
    }

    public TravelAlbum(String id, String albumTitle, String albumDate, String mainImgPath, String profileImgPath, String albumLocation) {
        this.id = id;
        this.albumTitle = albumTitle;
        this.albumDate = albumDate;
        this.mainImgPath = mainImgPath;
        this.profileImgPath = profileImgPath;
        this.albumLocation = albumLocation;
    }

    // GETTER


    public String getAlbumLocation() {
        return albumLocation;
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


    public void setAlbumLocation(String albumLocation) {
        this.albumLocation = albumLocation;
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
