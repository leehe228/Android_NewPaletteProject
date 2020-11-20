package com.anonymoushippo.palette;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {

    public static boolean getStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int TYPE = networkInfo.getType();
            switch (TYPE){
                case ConnectivityManager.TYPE_MOBILE:
                case ConnectivityManager.TYPE_WIFI: {
                    return true;
                }
            }
        }
        return false;
    }
}

