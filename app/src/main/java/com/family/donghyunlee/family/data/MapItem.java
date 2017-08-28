package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-24.
 */

public class MapItem {

    private String albumTitle;
    private String location;
    private String albumImage;
    private String album_key;
    private String date;
    private double makerLat;
    private double makerLon;

    public MapItem(){

    }

    public MapItem(String albumTitle, String location, String albumImage, String album_key, String date,
                   double makerLat, double makerLon) {
        this.albumTitle = albumTitle;
        this.location = location;
        this.albumImage = albumImage;
        this.album_key = album_key;
        this.makerLat = makerLat;
        this.makerLon = makerLon;
        this.date = date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public void setAlbum_key(String album_key) {
        this.album_key = album_key;
    }

    public void setMakerLat(double makerLat) {
        this.makerLat = makerLat;
    }

    public void setMakerLon(double makerLon) {
        this.makerLon = makerLon;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getLocation() {
        return location;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public String getDate() {
        return date;
    }

    public String getAlbum_key() {
        return album_key;
    }

    public double getMakerLat() {
        return makerLat;
    }

    public double getMakerLon() {
        return makerLon;
    }
}
