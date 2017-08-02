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
import android.widget.Button;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DONGHYUNLEE on 2017-07-29.
 */

public class PhotoSel extends AppCompatActivity {

    private static final String TAG = PhotoSel.class.getSimpleName();
    @BindView(R.id.photosel_camera)
    Button photoselCamera;
    @BindView(R.id.photosel_album)
    Button photoselAlbum;
    @BindView(R.id.photosel_cancel)
    Button photoselCancel;

    private Bitmap photo;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private Uri mImageCaptureUri;
    private String absolutePatt;

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

    // 카메라 촬영 후 이미지 가져오기
    private void doTakePhotoAction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 임시로 사용할 파일의 경로를 생성한다.
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    // 앨범에서 이미지 가져오기
    private void doTakeAlbumAction() {
        // 앨범 호출
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
                // 이후 처리가 카메라와 같음.
                mImageCaptureUri = data.getData();
                Log.d(TAG, "uri path: " + mImageCaptureUri.getPath().toString());

            case PICK_FROM_CAMERA:
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정한다.
                // 이후에 이미지 크롭 어플리케이션을 호출한다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // CROP할 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 200);    // CROP한 이미지의 x, y축 크기
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);      // CROP박스의 x, y축 비율
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);    // CROP_FROM_CAMERA case 이동
                break;
            case CROP_FROM_IMAGE:
                // 크롭이 된 이후의 이미지를 넘겨받느다.
                // 이미지뷰에 이미지를 보여주거나 부가적인 작업 이후에 임시 파일을 삭제한다.
                if (resultCode != RESULT_OK) {
                    return;
                }
                final Bundle extras = data.getExtras();
                // CROP된 이미지를 저장하기 위한 FILE 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel" + System.currentTimeMillis() + ".jpg";

                if (extras != null) {
                    // CROP된 BITMAP
                    photo = extras.getParcelable("data"); // 넘겨줄 데이터(poto)
                    storeCropImage(photo, filePath);    // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    absolutePatt = filePath;


                    //TODO '확인' 클릭 시 액티비티 종료 처리 finish()
                    // Data BUS POST
                    BusProvider.getInstance().post(new DataEvent(photo, mImageCaptureUri));
                    Log.e("TAG", ">>>>>>>>>>>>>>>       " + String.valueOf(mImageCaptureUri));
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

    }

    // Bitmap을 저장하는 부분
    private void storeCropImage(Bitmap bitmap, String filePath) {
        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if (!directory_SmartWheel.exists()) {  // SmartWheel 디렉토리 폴더가 없으면 (새로운 이미지를 저장할 경우)
            directory_SmartWheel.mkdir();
        }
        File copyFile = new File(filePath);
        BufferedOutputStream out;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 한다.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "storeCropImage Function");
        }
    }
}

