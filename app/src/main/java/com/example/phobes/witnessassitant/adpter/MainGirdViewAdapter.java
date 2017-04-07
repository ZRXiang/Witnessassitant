package com.example.phobes.witnessassitant.adpter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;

/**
 * Created by phobes on 2016/6/30.
 */
public class MainGirdViewAdapter extends BaseAdapter {
    private Context context;
    public MainGirdViewAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate
                (R.layout.baseadapter_provider, parent, false);

        ImageView mImageView = (ImageView)convertView.findViewById(R.id.main_function_image_view);
        mImageView.setImageResource(mThumbIds[position]);
        if(mImageView==null){
            Log.i("convertView:","null");
        }else {
            Log.i("convertView:",String.valueOf(mImageView.getWidth()));
        }
        TextView mTextView = (TextView)convertView.findViewById(R.id.main_function_text_view);
        mTextView.setText(context.getString(mFunctionTexts[position]));
        mTextView.setTextColor(Color.RED);
        return convertView;
       /* ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;*/
    }
    // references to our images
    private Integer[] mFunctionTexts = {
          R.string.entry_check_button,R.string.sample_guild,
            R.string.sample_witness_button,R.string.test_witness_button,
            R.string.sample_product_witness_button,R.string.sample_split_model_button,
            R.string.in_room_button,R.string.out_room_button
    };
    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.check, R.drawable.guild,
            R.drawable.sample, R.drawable.test,
            R.drawable.product, R.drawable.inroom,
    };
}
