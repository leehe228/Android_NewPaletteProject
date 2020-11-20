package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class PortfolioActivity extends BaseActivity {

    private final String[] keys = new String[1];
    private final String[] data = new String[1];
    private String result;

    /* Loading View */
    ImageView loadingView;
    View backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        // Loading
        loadingView = findViewById(R.id.LoadingView);
        backView = findViewById(R.id.LoadingBack);
        GlideDrawableImageViewTarget loadingImage = new GlideDrawableImageViewTarget(loadingView);
        Glide.with(this).load(R.drawable.loading).into(loadingImage);

        ImageButton homeButton = findViewById(R.id.HomeButton);
        ImageButton popularButton = findViewById(R.id.PopularButton);
        ImageButton searchButton = findViewById(R.id.SearchButton);
        ImageButton likeButton = findViewById(R.id.LikeButton);
        ImageButton portfolioButton = findViewById(R.id.PortfolioButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopularActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.VISIBLE);
                keys[0] = "email";
                data[0] = "test@test.com";

                new Thread() {
                    public void run() {
                        String resultString = HttpPostData.POST("account/getLike/", keys, data);

                        if (resultString.equals("-1") || resultString.equals("SEND_FAIL")) {
                            //
                        } else {
                            String[] resultList = resultString.split("-");

                            ArrayList<Bitmap> tempList = new ArrayList<Bitmap>();

                            try {
                                for (String code : resultList) {
                                    java.net.URL url = new java.net.URL("http://141.164.40.63:8000/media/database/" + code + "/1.jpg");
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream input = connection.getInputStream();
                                    tempList.add(BitmapFactory.decodeStream(input));
                                }

                                UserLike.setImages(tempList);
                                Message msg = likeOpenHandler.obtainMessage();
                                likeOpenHandler.sendMessage(msg);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        });

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler likeOpenHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            Intent intent = new Intent(getApplicationContext(), LikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    };

    public void onTitleClicked(View view){

    }

    @Override
    public void onBackPressed(){
        finish();
    }
}