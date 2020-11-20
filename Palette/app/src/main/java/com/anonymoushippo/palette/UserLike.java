package com.anonymoushippo.palette;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

public class UserLike {

    public static ArrayList<Bitmap> userLikeImages = new ArrayList<>();

    public UserLike(){

    }

    public static void Clear(){
        userLikeImages.clear();
    }

    public static void setImages(ArrayList<Bitmap> temp){
        userLikeImages = temp;
        Log.i("SAVED", String.valueOf(userLikeImages.size()));
    }

    public static ArrayList<Bitmap> getImages(){
        Log.i("LOADING", String.valueOf(userLikeImages.size()));
        return userLikeImages;
    }
}
