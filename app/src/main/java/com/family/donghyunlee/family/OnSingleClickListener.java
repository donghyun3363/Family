package com.family.donghyunlee.family;

import android.os.SystemClock;
import android.view.View;

/**
 * Created by DONGHYUNLEE on 2017-08-29.
 */

public abstract class OnSingleClickListener  implements View.OnClickListener{
    // 중복 클릭 방지 시간 설정
    private static final long MIN_CLICK_INTERVAL=300;

    private long mLastClickTime;

    public abstract void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        long currentClickTime= SystemClock.uptimeMillis();
        long elapsedTime=currentClickTime-mLastClickTime;
        mLastClickTime=currentClickTime;

        // 중복 클릭인 경우
        if(elapsedTime<=MIN_CLICK_INTERVAL){
            return;
        }

        // 중복 클릭아 아니라면 추상함수 호출
        onSingleClick(v);
    }


}
