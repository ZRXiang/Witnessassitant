package com.example.phobes.witnessassitant.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.phobes.witnessassitant.R;

/**
 * Created by phobes on 2016/6/3.
 */
public class SampleGuildItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "sample_guild_item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private int sample_guild_id=0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SampleGuildItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            sample_guild_id = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
            sample_guild_id=getArguments().getInt(ARG_ITEM_ID);
            System.out.print(sample_guild_id);
            Activity activity = this.getActivity();
          /*  CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }*/
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sample_guild_item_detail, container, false);
        WebView mWebView= (WebView) rootView.findViewById(R.id.sample_guild_web_view);
        // Show the dummy content as text in a TextView.
        if (sample_guild_id != 0) {

            String filePath = "file:///android_asset/www/"+sample_guild_id+".html";
            mWebView.loadUrl(filePath);
        }

        return rootView;
    }
}


