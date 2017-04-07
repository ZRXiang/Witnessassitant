package com.example.phobes.witnessassitant.adpter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.phobes.witnessassitant.fragment.EntryCheckHistory;
import com.example.phobes.witnessassitant.fragment.EntryCheckListFragment;
import com.example.phobes.witnessassitant.fragment.EntryCheckTaskFragment;
import com.example.phobes.witnessassitant.fragment.WitnessHistoryFragment;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckFragmentPagerAdapter extends FragmentPagerAdapter {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"任务下载","验收","历史记录"};
    private Context context;
    public EntryCheckFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        sharedPreferences = context.getSharedPreferences("nPage", 0);

        editor = sharedPreferences.edit();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
//                editor.putBoolean("visible",false);
                fragment =  new EntryCheckTaskFragment();
                break;
            case 1:
//                editor.putBoolean("visible",true);
                fragment = new EntryCheckListFragment();
                break;
            case 2:
//                editor.putBoolean("visible",true);
                fragment = new EntryCheckHistory();
                break;
        }
//        editor.commit();
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