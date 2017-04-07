package com.example.phobes.witnessassitant.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.activity.EntryCheckMainActivity;
import com.example.phobes.witnessassitant.activity.TestObjectListActivity;
import com.example.phobes.witnessassitant.activity.WitnessMainActivity;
import com.example.phobes.witnessassitant.model.TestObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phobes on 2016/6/7.
 */
public class GroupFragment extends Fragment {
    View sampleClassifyView = null;
    private List<TestObject.TestObjectItem> sampleGuildItems = new ArrayList<TestObject.TestObjectItem>();
    TextView toolsTextViews[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String[] groups = {"全部", "钢筋及金属材料", "原材料", "混凝土", "土工", "其他试验", "现场检测"};
        View rootview = inflater.inflate(R.layout.object_group, container, false);
        sharedPreferences = getContext().getSharedPreferences("nPage", 0);
        editor = sharedPreferences.edit();
        LinearLayout linearLayout = (LinearLayout) rootview.findViewById(R.id.group_tools);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout.LayoutParams imglayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
        toolsTextViews = new TextView[groups.length];
        for (int i = 0; i < groups.length; i++) {
            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(layoutParams);
            textView.setText(groups[i]);
            textView.setTextSize(16);
            textView.setPadding(16, 16, 16, 16);
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTextColor(finalI);
                    changeChildList(finalI);
                }
            });
            toolsTextViews[i] = textView;
            linearLayout.addView(textView);
            if (i != groups.length - 1) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setBackgroundColor(Color.GRAY);
                imageView.setLayoutParams(imglayoutParams);
                linearLayout.addView(imageView);
            }
        }
        changeTextColor(0);
        return rootview;
    }

    private void changeTextColor(int id) {
        for (int i = 0; i < toolsTextViews.length; i++) {
            if (i != id) {
                toolsTextViews[i].setBackgroundResource(android.R.color.darker_gray);
                toolsTextViews[i].setTextColor(0xff000000);
            }
        }
        toolsTextViews[id].setBackgroundResource(R.color.white_grap);
        toolsTextViews[id].setTextColor(0xffff5d5e);
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private void changeChildList(int i) {
        int object_id = getObjectId(i);
        editor.putInt("npage", 1);
        editor.putInt("nObjectId",object_id);
        editor.commit();

        if (getActivity() instanceof TestObjectListActivity) {
            TestObjectListFragment fragment = (TestObjectListFragment) getFragmentManager().findFragmentById(R.id.sample_guild_list_frameLayout);
            fragment.changeTestObjects(object_id);
        } else if (getActivity() instanceof WitnessMainActivity) {
            WitenessTaskListFragment fragment = (WitenessTaskListFragment) getFragmentManager().findFragmentById(R.id.sample_guild_list_frameLayout);
            fragment.changeTaskByObjId(object_id);
        } else if (getActivity() instanceof EntryCheckMainActivity) {
            EntryCheckTaskListDetailFragment fragment = (EntryCheckTaskListDetailFragment) getFragmentManager().findFragmentById(R.id.sample_guild_list_frameLayout);
            fragment.changeTaskByObjId(object_id);
        }
    }

    private int getObjectId(int position) {

        if (position == 0) {
            return -1;
        } else if (position == 1) {
            return 10;
        } else if (position == 2) {
            return 11;
        } else if (position == 3) {
            return 12;
        } else if (position == 4) {
            return 13;
        } else if (position == 5) {
            return 14;
        } else if (position == 6) {
            return 15;
        }
        return -1;
    }
}
