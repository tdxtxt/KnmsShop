package com.knms.shop.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.knms.shop.android.fragment.base.BaseFragment;

import java.util.List;

import static android.R.attr.data;

/**
 * Created by tdx on 2016/10/17.
 */

public class CommFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<BaseFragment> fragments;
    public CommFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragments){
        super(fm);
        this.fragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
}
