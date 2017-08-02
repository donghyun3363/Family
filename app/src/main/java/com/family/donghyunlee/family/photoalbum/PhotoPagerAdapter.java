package com.family.donghyunlee.family.photoalbum;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
 */

public class PhotoPagerAdapter extends FragmentStatePagerAdapter {

    public PhotoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return MemoryFragment.newInstance();
            case 1:
                return TravelFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
