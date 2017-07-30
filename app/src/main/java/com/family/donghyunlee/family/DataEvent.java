package com.family.donghyunlee.family;

import android.graphics.Bitmap;

/**
 * Created by DONGHYUNLEE on 2017-07-30.
 */

public class DataEvent {


    private Bitmap photo;


    DataEvent(Bitmap photo){
        this.photo = photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
