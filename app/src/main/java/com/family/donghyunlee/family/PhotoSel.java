package com.family.donghyunlee.family;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-29.
 */

public class PhotoSel extends AppCompatActivity {

    private static final String TAG = PhotoSel.class.getSimpleName();


    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private Uri mImageCaptureUri;
    private Bitmap photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photosel);
        ButterKnife.bind(this);
    }

    // 카메라 촬영후 가져오기 클릭
    @OnClick(R.id.photosel_camera)
    void camereClick() {
        doTakePhotoAction();
    }

    // 카메라 앨범에서 가져오기 클릭
    @OnClick(R.id.photosel_album)
    void albumClick() {
        doTakeAlbumAction();
    }

    // 취소
    @OnClick(R.id.photosel_cancel)
    void cancelClick() {
        finish();
    }

    @OnClick(R.id.photosel_back)
    void backClick(){
        finish();
    }
    // 카메라 촬영 후 이미지 가져오기
    private void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 임시로 파일을 생성한다.
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        // 다음 방식으로 url 파일명으로 파일을 생성하여 uri로 가져오고 해당 uri에 저장해 달라고 요청하는 것.
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    private void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_ALBUM:
                // 앨범에서 uri를 받을 때
                mImageCaptureUri = data.getData();

            case PICK_FROM_CAMERA:
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정한다.
                // 이후에 이미지 크롭 어플리케이션을 호출한다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                Log.i(TAG, ">>>>>>>>>>>>>>>> mImageCaptureUri22222222222: " +mImageCaptureUri );
                Log.i(TAG, ">>>>>>>>>>>>>>>>1");
                // CROP할 이미지를 400~ 600 크기로 저장
                intent.putExtra("outputX", 200);    // CROP한 이미지의 x, y축 크기
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);      // CROP박스의 x, y축 비율
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);    // 아마 해상도 조정하는 듯
                intent.putExtra("return-data", true);
                Log.i(TAG, ">>>>>>>>>>>>>>>>2");
                startActivityForResult(intent, CROP_FROM_IMAGE);
                Log.i(TAG, ">>>>>>>>>>>>>>>>3");
                break;
            case CROP_FROM_IMAGE:
                // 크롭이 된 이후의 이미지를 넘겨받는다.
                // 이미지뷰에 이미지를 보여주거나 부가적인 작업 이후에 임시 파일을 삭제한다.
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, ">>>>>>>>>     RESULT_OK NOT" );
                    return;
                }
                Log.i(TAG, ">>>>>>>>>>>>>>>>4");
                final Bundle extras = data.getExtras();
                // CROP된 이미지를 저장하기 위한 FILE 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel" + System.currentTimeMillis() + ".jpg";
                Log.i(TAG, ">>>>>>>>>>>>>>>>5");
                if (extras != null) {
                    // CROP된 BITMAP
                    photo = extras.getParcelable("data");
                    storeCropImage(photo, filePath);          // 비트맵을 jpg로 변형후 file로 가져옴.
                    Intent sendIntent = new Intent();
                    sendIntent.putExtra("IMAGE_URI", mImageCaptureUri);
                    setResult(RESULT_OK, sendIntent);
                    Log.i(TAG, ">>>>>>>>>>>>>>>>6");

                    break;
                }
                // 임시파일 삭제
                File file = new File(mImageCaptureUri.getPath());
                if (file.exists()) {
                    file.delete();
                }
                break;
            default:
                break;
        }
        finish();

    }

    // Bitmap을 저장하는 부분
    private void storeCropImage(Bitmap bitmap, String filePath) {
        // dongs 폴더를 생성하여 이미지를 저장하는 방식
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if (!directory_SmartWheel.exists()) {  // dongs 디렉토리 폴더가 없으면 (새로운 이미지를 저장할 경우)
            directory_SmartWheel.mkdir();
        }

        File copyFile = new File(filePath); //  Uri.fromFile(copyFile) 이런 식으로 File 포인터로 uri가져올 수 있음
        BufferedOutputStream out;
        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 한다. ?
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "storeCropImage Function");
        }
        Log.i(TAG, ">>>>>>>>>>>>>>>> filePath: " +filePath );

    }
}

