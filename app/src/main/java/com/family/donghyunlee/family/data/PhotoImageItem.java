package com.family.donghyunlee.family.data;

import android.graphics.Bitmap;

/**
 * Created by DONGHYUNLEE on 2017-08-05.
 */

public class PhotoImageItem {


    private Bitmap photo;

    private String inAlbumDate;

    PhotoImageItem(){

    }

    public PhotoImageItem(Bitmap photo){
        this.photo = photo;
    }


    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
