package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.mmin18.widget.RealtimeBlurView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GalleryInformationActivity extends BaseActivity {

    private final String[] keys = new String[1];
    private final String[] data = new String[1];
    private String result;

    ImageView backgroundImageView;
    RealtimeBlurView backBlurView;
    SquareImageView mainImageView;
    TextView titleTextView, creatorTextView, dateTextView, categoryTextView, infoTextView;
    Button enterButton;
    String artTitles, artContents, artNumber;

    private String title, creator, date, category, info;

    private ImageView loadingView;

    private String CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_information);

        Intent intent = getIntent();
        CODE = intent.getStringExtra("CODE");

        GradientDrawable drawable = (GradientDrawable) this.getDrawable(R.drawable.background_round);

        // 초기화
        init();

        // 인스턴스화
        backgroundImageView = findViewById(R.id.GalleryInfo_background);
        backBlurView = findViewById(R.id.GalleryInfo_Blur_background);
        mainImageView = findViewById(R.id.GalleryInfo_mainView);
        titleTextView = findViewById(R.id.GalleryInfo_title);
        creatorTextView = findViewById(R.id.GalleryInfo_creator);
        dateTextView = findViewById(R.id.GalleryInfo_date);
        categoryTextView = findViewById(R.id.GalleryInfo_category);
        infoTextView = findViewById(R.id.GalleryInfo_info);

        loadingView = findViewById(R.id.LoadingView);
        loadingView.setVisibility(View.VISIBLE);
        GlideDrawableImageViewTarget loadingImage = new GlideDrawableImageViewTarget(loadingView);
        Glide.with(this).load(R.drawable.loading).into(loadingImage);

        enterButton = findViewById(R.id.GalleryInfo_enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                intent.putExtra("CODE", CODE);
                intent.putExtra("TITLES", artTitles);
                intent.putExtra("CONTENTS", artContents);
                intent.putExtra("NUMBER", artNumber);
                Log.e("DATA", CODE + artTitles + artContents + artNumber);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        mainImageView.setBackground(drawable);
        mainImageView.setClipToOutline(true);
    }

    public void init(){

        keys[0] = "code";
        data[0] = CODE;

        new Thread() {
            public void run() {
                result = HttpPostData.POST("gallery/getExhibition/", keys, data);

                Log.i("RESULT", result);

                if (result.equals("") || result.equals("-1") || result.equals("SEND_FAIL")) {

                    title = "";
                    creator = "";
                    date = "";
                    category = "";
                    info = "";
                    enterButton.setEnabled(false);
                    enterButton.setVisibility(View.INVISIBLE);

                } else {
                    String[] resultList = result.split("&");

                    title = resultList[0].trim();
                    creator = resultList[1].trim();
                    info = resultList[2].trim();
                    artNumber = resultList[3];
                    artTitles = resultList[4].trim();
                    artContents = resultList[5].trim();
                    date = resultList[6].trim();
                    category = resultList[7].trim();

                }

                Message msg = initHandler.obtainMessage();
                initHandler.sendMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    Handler initHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            titleTextView.setText(title);
            creatorTextView.setText(creator);
            dateTextView.setText(date);

            String[] titles = {"사진전", "가구전시", "유아", "미디어 아트", "학생 작품", "제품 디자인", "캐릭터", "패션", "레고 전시"};
            char[] cat = category.toCharArray();
            String catString = "";

            for (int j = 0; j < cat.length; j++) {
                if (cat[j] == '1') {
                    catString = catString.concat("#" + titles[j] + " ");
                }
            }
            catString += " ";

            String dateTemp = "~" + date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6) + ".";

            titleTextView.setText(title);
            creatorTextView.setText(creator);
            dateTextView.setText(dateTemp);
            categoryTextView.setText(catString);
            infoTextView.setText(info);

            Glide.with(getApplicationContext()).load("http://141.164.40.63:8000/media/database/" + CODE + "/1.jpg").into(mainImageView);
            Glide.with(getApplicationContext()).load("http://141.164.40.63:8000/media/database/" + CODE + "/1.jpg").into(backgroundImageView);
            loadingView.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}