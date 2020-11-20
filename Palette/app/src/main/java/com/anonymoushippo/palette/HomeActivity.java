package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.mmin18.widget.RealtimeBlurView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<String> randomCodeList;
    private ArrayList<Integer> randomNumberList;

    private final String[] keys = new String[1];
    private final String[] data = new String[1];
    private String result;

    SwipeRefreshLayout mSwipeRefreshLayout;

    SquareImageView[] imageViews;

    SquareImageView popUpView;
    RealtimeBlurView backBlurView;
    LinearLayout popUpInfoView;
    TextView popUpTitleTextView, popUpCreatorTextView;
    View popUpInnerView;

    private String infoResult;

    private int FLAG;

    private Animation popUpAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Grid View
        randomCodeList = new ArrayList<>();
        randomNumberList = new ArrayList<>();

        // Swipe Refresh View
        mSwipeRefreshLayout = findViewById(R.id.Main_SwipeLayout_main);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // View Pager
        ViewPager mainViewPager = findViewById(R.id.Main_ViewPager_main);
        ViewPagerAdapter scrollAdapter = new ViewPagerAdapter(this);
        mainViewPager.setAdapter(scrollAdapter);

        GradientDrawable drawable = (GradientDrawable) this.getDrawable(R.drawable.background_round);

        imageViews = new SquareImageView[18];
        popUpView = findViewById(R.id.Main_ImageView_PopUp);
        backBlurView = findViewById(R.id.Main_blurView_layer);
        popUpInfoView = findViewById(R.id.Main_InfoView_PopUp);
        popUpTitleTextView = findViewById(R.id.Main_TextView_PopUpTitle);
        popUpCreatorTextView = findViewById(R.id.Main_TextView_PopUpCreator);
        popUpInnerView = findViewById(R.id.Main_InnerView_PopUp);

        ImageButton homeButton = findViewById(R.id.HomeButton);
        ImageButton popularButton = findViewById(R.id.PopularButton);
        ImageButton searchButton = findViewById(R.id.SearchButton);
        ImageButton likeButton = findViewById(R.id.LikeButton);
        ImageButton portfolioButton = findViewById(R.id.PortfolioButton);

        popUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pop_up);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = refreshHandler.obtainMessage();
                refreshHandler.sendMessage(msg);
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
                keys[0] = "email";
                data[0] = "test@test.com";

                new Thread() {
                    public void run() {
                        String resultString = HttpPostData.POST("account/getLike/", keys, data);

                        if (result.equals("-1") || result.equals("SEND_FAIL")) {
                            //
                        } else {
                            String[] resultList = resultString.split("-");
                            Log.i("RESULT", result);

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

        // 초기화
        FLAG = 0;

        // Listener
        View.OnLongClickListener onLongClickListener = new ImageButton.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                for(int i = 0; i < imageViews.length; i++){
                    if(v.getId() == imageViews[i].getId()){
                        Glide.with(getApplicationContext()).load("http://141.164.40.63:8000/media/database/" + randomCodeList.get(i) + "/" + randomNumberList.get(i) + ".jpg").into(popUpView);
                        FLAG = 1;
                        int finalI = i;
                        new Thread() {
                            public void run() {
                                infoResult = HttpPostData.POST("gallery/getSimpleInfo/", new String[]{"code"}, new String[]{randomCodeList.get(finalI)});
                                Message msg = setInfoPopUpHandler.obtainMessage();
                                setInfoPopUpHandler.sendMessage(msg);
                            }
                        }.start();
                        break;
                    }
                }
                return true;
            }
        };

        imageViews[0] = findViewById(R.id.Main_ImageView_1);
        imageViews[1] = findViewById(R.id.Main_ImageView_2);
        imageViews[2] = findViewById(R.id.Main_ImageView_3);

        imageViews[3] = findViewById(R.id.Main_ImageView_4);
        imageViews[4] = findViewById(R.id.Main_ImageView_5);
        imageViews[5] = findViewById(R.id.Main_ImageView_6);

        imageViews[6] = findViewById(R.id.Main_ImageView_7);
        imageViews[7] = findViewById(R.id.Main_ImageView_8);
        imageViews[8] = findViewById(R.id.Main_ImageView_9);

        imageViews[9] = findViewById(R.id.Main_ImageView_10);
        imageViews[10] = findViewById(R.id.Main_ImageView_11);
        imageViews[11] = findViewById(R.id.Main_ImageView_12);

        imageViews[12] = findViewById(R.id.Main_ImageView_13);
        imageViews[13] = findViewById(R.id.Main_ImageView_14);
        imageViews[14] = findViewById(R.id.Main_ImageView_15);

        imageViews[15] = findViewById(R.id.Main_ImageView_16);
        imageViews[16] = findViewById(R.id.Main_ImageView_17);
        imageViews[17] = findViewById(R.id.Main_ImageView_18);

        for (ImageView imageView : imageViews) {
            imageView.setBackground(drawable);
            imageView.setClipToOutline(true);
            imageView.setOnLongClickListener(onLongClickListener);
        }

        popUpView.setBackground(drawable);
        popUpView.setClipToOutline(true);

        init();
    }

    public void init(){

        keys[0] = "email";
        data[0] = "test@test.com";

        new Thread() {
            public void run() {
                result = HttpPostData.POST("account/getInfo/", keys, data);

                if (result.equals("") || result.equals("-1") || result.equals("SEND_FAIL")) {

                } else {
                    keys[0] = "interest";
                    data[0] = result.split("&")[2];
                    result = HttpPostData.POST("gallery/suggestion/", keys, data);

                    String[] resultList = result.split("-");

                    ArrayList<String> recCodeList = new ArrayList<>();
                    randomCodeList = new ArrayList<>();

                    Collections.addAll(recCodeList, resultList);

                    ArrayList<Integer> codeList = MyUtility.Random(resultList.length, recCodeList.size());

                    Random random = new Random();

                    for(Integer integer : codeList){
                        randomCodeList.add(recCodeList.get(integer));
                        randomNumberList.add(random.nextInt(3) + 1);
                    }

                    Message msg = initHandler.obtainMessage();
                    initHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        Message msg = refreshHandler.obtainMessage();
        refreshHandler.sendMessage(msg);
    }

    @SuppressLint("HandlerLeak")
    Handler refreshHandler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            init();
        }
    };

    @SuppressLint("HandlerLeak")
    Handler initHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            int i = 0;
            for (int j = 0; j < Math.min(18, randomCodeList.size()); j++) {
                Glide.with(getApplicationContext()).load("http://141.164.40.63:8000/media/database/" + randomCodeList.get(j) + "/" + randomNumberList.get(j) + ".jpg").into(imageViews[i++]);
            }
        }
    };

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

    @SuppressLint("HandlerLeak")
    Handler setInfoPopUpHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            popUpTitleTextView.setText(infoResult.split("&")[0]);
            popUpCreatorTextView.setText(infoResult.split("&")[1]);

            popUpView.setVisibility(View.VISIBLE);
            backBlurView.setVisibility(View.VISIBLE);
            popUpInfoView.setVisibility(View.VISIBLE);
            popUpInnerView.setVisibility(View.VISIBLE);

            popUpView.startAnimation(popUpAnimation);
            popUpInfoView.startAnimation(popUpAnimation);
            popUpInnerView.startAnimation(popUpAnimation);
        }
    };

    public void onTitleClicked(View view){
        Message msg = refreshHandler.obtainMessage();
        refreshHandler.sendMessage(msg);
    }

    public void onBackBlurClicked(View view){
        popUpView.setVisibility(View.INVISIBLE);
        backBlurView.setVisibility(View.INVISIBLE);
        popUpInfoView.setVisibility(View.INVISIBLE);
        popUpInnerView.setVisibility(View.INVISIBLE);

        popUpTitleTextView.setText("");
        popUpCreatorTextView.setText("");
        FLAG = 0;
    }

    @Override
    public void onBackPressed(){
        if (FLAG == 0){
            finish();
        }
        else if(FLAG == 1){
            popUpView.setVisibility(View.INVISIBLE);
            backBlurView.setVisibility(View.INVISIBLE);
            popUpInfoView.setVisibility(View.INVISIBLE);
            popUpInnerView.setVisibility(View.INVISIBLE);

            popUpTitleTextView.setText("");
            popUpCreatorTextView.setText("");
            FLAG = 0;
        }
    }
}