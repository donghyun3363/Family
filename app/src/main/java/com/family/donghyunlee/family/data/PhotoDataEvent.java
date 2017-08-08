package com.family.donghyunlee.family.data;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by DONGHYUNLEE on 2017-07-30.
 */

public class PhotoDataEvent {

    private Bitmap photo;
    private Uri filePath;

    public PhotoDataEvent(Bitmap photo, Uri filePath) {
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
