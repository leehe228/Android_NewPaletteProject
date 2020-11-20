package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomeSquareAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> codes;

    public HomeSquareAdapter(Context context, ArrayList<String> codeList){
        this.context = context;
        this.codes = codeList;
    }

    @Override
    public int getCount() {
        return codes.size();
    }

    @Override
    public Object getItem(int position) {
        return codes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.main_square, parent, false);

        ImageView mainImageView = view.findViewById(R.id.Square_ImageView);

        GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.background_round);
        mainImageView.setBackground(drawable);
        mainImageView.setClipToOutline(true);
        System.out.println(codes.get(position));

        Glide.with(context).load("http://141.164.40.63:8000/media/database/" + codes.get(position) + "/1.jpg").into(mainImageView);

        return view;
    }
}
