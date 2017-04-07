package com.example.phobes.witnessassitant.adpter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.phobes.witnessassitant.fragment.SiteTestHistory;
import com.example.phobes.witnessassitant.fragment.SiteTestListFragment;
import com.example.phobes.witnessassitant.fragment.SiteTestTaskFragment;


/**
 * Created by phobes on 2016/6/27.
 * Alter  2016/9/2
 */
public class SiteTestFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{ "任务", "历史记录"};
    private Context context;

    public SiteTestFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
           /* case 0:
                fragment = new SiteTestTaskFragment();
                break;*/
            case 0:
                fragment = new SiteTestListFragment();
                break;
            case 1:
                fragment = new SiteTestHistory();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}