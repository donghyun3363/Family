package com.family.donghyunlee.family;

import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by DONGHYUNLEE on 2017-08-20.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter
{
    public MainPagerAdapter(android.support.v4.app.FragmentManager fm)
    {
        super(fm);
    }
    @Override
    public android.support.v4.app.Fragment getItem(int position)
    {
        switch(position)
        {
            case 0:
                return new MainFirstFragment();
            case 1:
                return new MainSecondFragment();
            case 2:
                return new MainThirdFragment();
            default:
                return null;
        }
    }
    @Override
    public int getCount()
    {
        return 3;
    }
}
