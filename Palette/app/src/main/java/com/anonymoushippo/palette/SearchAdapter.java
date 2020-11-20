package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class SearchAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String> sample;

    public SearchAdapter(Context context, ArrayList<String> data){
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public String getItem(int position) {
        return sample.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.search_listview, null);

        GradientDrawable drawable = (GradientDrawable) mContext.getDrawable(R.drawable.background_round);

        TextView titleTextView = view.findViewById(R.id.SearchList_TextView_title);
        TextView creatorTextView = view.findViewById(R.id.SearchList_TextView_creator);
        TextView dateTextView = view.findViewById(R.id.SearchList_TextView_date);
        TextView categoryTextView = view.findViewById(R.id.SearchList_TextView_category);

        ImageView mainImageView = view.findViewById(R.id.SearchList_ImageView);

        String CODE = sample.get(position).split("-")[0];
        Random random = new Random();
        int r = random.nextInt(3) + 1;
        titleTextView.setText(sample.get(position).split("-")[1]);
        creatorTextView.setText(sample.get(position).split("-")[2]);
        dateTextView.setText(sample.get(position).split("-")[3]);
        categoryTextView.setText(sample.get(position).split("-")[4]);

        Glide.with(mContext).load("http://141.164.40.63:8000/media/database/" + CODE + "/" + r + ".jpg").into(mainImageView);
        mainImageView.setBackground(drawable);
        mainImageView.setClipToOutline(true);

        return view;
    }
}

