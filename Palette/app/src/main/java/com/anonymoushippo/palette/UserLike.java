package com.anonymoushippo.palette;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

public class UserLike {

    public static ArrayList<Bitmap> userLikeImages = new ArrayList<>();
    public static ArrayList<String> userLikeCodes = new ArrayList<>();

    public UserLike(){

    }

    public static void Clear(){
        userLikeImages.clear();
        userLikeCodes.clear();
    }

    public static void setImages(ArrayList<Bitmap> temp){
        userLikeImages = temp;
    }

    public static ArrayList<Bitmap> getImages(){
        return userLikeImages;
    }

    public static void setCodes(ArrayList<String> temp){
        userLikeCodes = temp;
    }

    public static ArrayList<String> getCodes(){
        return userLikeCodes;
    }
}
