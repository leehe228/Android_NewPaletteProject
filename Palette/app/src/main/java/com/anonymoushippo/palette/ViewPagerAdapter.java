package com.anonymoushippo.palette;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext = null;
    private ArrayList<Integer> imageList;

    public ViewPagerAdapter(Context context){
        mContext = context;
        imageList = new ArrayList<>();
        imageList.add(R.drawable.v1);
        imageList.add(R.drawable.v2);
        imageList.add(R.drawable.v3);
        imageList.add(R.drawable.v4);
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null ;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.viewpage, container, false);

        ImageView mainImageView = view.findViewById(R.id.ViewPager_ImageView_main);
        mainImageView.setImageResource(imageList.get(position));
        container.addView(view) ;
        return view ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }
}

