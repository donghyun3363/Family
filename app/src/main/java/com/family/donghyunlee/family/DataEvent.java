package com.family.donghyunlee.family;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by DONGHYUNLEE on 2017-07-30.
 */

public class DataEvent {

    private Bitmap photo;
    private Uri filePath;

    DataEvent(Bitmap photo, Uri filePath) {
        this.photo = photo;
        this.filePath = filePath;
    }

    public Uri getFilePath() {
        return filePath;
    }

    public Bitmap getPhoto() {
        return photo;
    }


    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
