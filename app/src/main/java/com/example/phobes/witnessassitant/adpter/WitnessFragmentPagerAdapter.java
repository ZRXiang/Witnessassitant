package com.example.phobes.witnessassitant.adpter;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.example.phobes.witnessassitant.fragment.WitnessHistoryFragment;
import com.example.phobes.witnessassitant.fragment.WitenessApplyListFragment;
import com.example.phobes.witnessassitant.fragment.WitenessListFragment;


public class WitnessFragmentPagerAdapter extends FragmentPagerAdapter{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"见证任务","见证","历史记录"};
    private Context context;
    private String type;


    public void setType(String type) {
        this.type = type;
    }

    public WitnessFragmentPagerAdapter(FragmentManager fm, Context context) {
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
                WitenessApplyListFragment witenessApplyListFragment=new WitenessApplyListFragment();
                witenessApplyListFragment.setType(type);
                fragment =  witenessApplyListFragment;
                break;
            case 1:
//                editor.putBoolean("visible",true);
                WitenessListFragment witenessListFragment=new WitenessListFragment();
                witenessListFragment.setType(type);
                fragment = witenessListFragment;
                break;
            case 2:
//                editor.putBoolean("visible",true);
                WitnessHistoryFragment witnessHistoryFragment=new WitnessHistoryFragment();
                witnessHistoryFragment.setType(type);
                fragment = witnessHistoryFragment;
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