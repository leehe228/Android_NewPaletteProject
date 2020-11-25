package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;

public class GalleryEndActivity extends BaseActivity {

    private final String[] keys = new String[1];
    private final String[] data = new String[1];
    private String result;

    private String CATEGORY;

    private Boolean LOVE_FLAG;

    private SquareImageView[] imageViews;

    private ArrayList<String> codeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_end);
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.parseColor("#131313"));

        Intent intent = getIntent();
        CATEGORY = intent.getStringExtra("CATEGORY");
        String CREATOR = intent.getStringExtra("CREATOR");

        // 초기화
        LOVE_FLAG = false;
        GradientDrawable drawable = (GradientDrawable) this.getDrawable(R.drawable.background_round);
        codeList = new ArrayList<>();

        // 인스턴스화
        TextView creatorTextView = findViewById(R.id.Portfolio_TextView_name);
        ImageButton loveButton = findViewById(R.id.GalleryEnd_loveButton);

        imageViews = new SquareImageView[3];
        imageViews[0] = findViewById(R.id.GalleryEnd_ImageView_1);
        imageViews[1] = findViewById(R.id.GalleryEnd_ImageView_2);
        imageViews[2] = findViewById(R.id.GalleryEnd_ImageView_3);

        for(SquareImageView squareImageView : imageViews){
            squareImageView.setBackground(drawable);
            squareImageView.setClipToOutline(true);
        }

        imageViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryInformationActivity.class);
                intent.putExtra("CODE", codeList.get(0));
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        imageViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryInformationActivity.class);
                intent.putExtra("CODE", codeList.get(1));
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        imageViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryInformationActivity.class);
                intent.putExtra("CODE", codeList.get(2));
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        creatorTextView.setText(CREATOR);

        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LOVE_FLAG){
                    LOVE_FLAG = false;
                    loveButton.setImageResource(R.drawable.un_love);
                }
                else{
                    LOVE_FLAG = true;
                    loveButton.setImageResource(R.drawable.love);
                }
            }
        });

        init();
    }

    public void init(){
        keys[0] = "interest";
        data[0] = CATEGORY;

        new Thread() {
            public void run() {
                result = HttpPostData.POST("gallery/suggestion/", keys, data);

                String[] resultList = result.split("-");

                Log.e("RESULT", result);

                codeList.addAll(Arrays.asList(resultList).subList(0, resultList.length));

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
            for (int j = 0; j < imageViews.length; j++) {
                Glide.with(getApplicationContext()).load("http://141.164.40.63:8000/media/database/" + codeList.get(j) + "/1.jpg").into(imageViews[j]);
            }
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