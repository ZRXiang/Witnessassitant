package com.example.phobes.witnessassitant.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;

/**
 * Created by phobes on 2016/6/7.
 */
public abstract class BasicTabFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.txt_content);
        textView.setText(initContent());
        return view;
    }

    public abstract String initContent();
}
