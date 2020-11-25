package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.mmin18.widget.RealtimeBlurView;

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

    TextView nameTextView, emailTextView;
    private String userEmail, userName;

    // FAB
    ImageButton fabEditButton, fabSettingButton, fabMainButton;
    TextView fabEditTextView, fabSettingTextView;
    RealtimeBlurView backBlurView;

    private Boolean FAB_FLAG;

    // 애니메이션
    private Animation fab_open, fab_close;
    private Animation FABOpenAnimation, FABCloseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        userEmail = preferences.getString("userEmail", "test@test.com");

        // Loading
        loadingView = findViewById(R.id.LoadingView);
        backView = findViewById(R.id.LoadingBack);
        GlideDrawableImageViewTarget loadingImage = new GlideDrawableImageViewTarget(loadingView);
        Glide.with(this).load(R.drawable.loading).into(loadingImage);
        loadingView.setVisibility(View.INVISIBLE);

        ImageButton homeButton = findViewById(R.id.HomeButton);
        ImageButton popularButton = findViewById(R.id.PopularButton);
        ImageButton searchButton = findViewById(R.id.SearchButton);
        ImageButton likeButton = findViewById(R.id.LikeButton);
        ImageButton portfolioButton = findViewById(R.id.PortfolioButton);

        nameTextView = findViewById(R.id.Portfolio_TextView_name);
        emailTextView = findViewById(R.id.Portfolio_TextView_contact);

        // FAB
        fabEditButton = findViewById(R.id.Main_FAB_edit);
        fabEditTextView = findViewById(R.id.Main_FABText_edit);

        fabMainButton = findViewById(R.id.Main_FAB_main);

        fabSettingButton = findViewById(R.id.Main_FAB_setting);
        fabSettingTextView = findViewById(R.id.Main_FABText_setting);
        backBlurView = findViewById(R.id.Main_blurView);

        FABOpenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab);
        FABCloseAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        FAB_FLAG = false;

        init();

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
                data[0] = userEmail;

                new Thread() {
                    public void run() {
                        String resultString = HttpPostData.POST("account/getLike/", keys, data);

                        if (resultString.equals("-1") || resultString.equals("SEND_FAIL")) {
                            //
                        } else {
                            if (resultString.equals("")) {
                                UserLike.setImages(null);
                                Message msg = noLikeOpenHandler.obtainMessage();
                                noLikeOpenHandler.sendMessage(msg);
                            } else {
                                String[] resultList = resultString.split("-");

                                ArrayList<Bitmap> tempList = new ArrayList<Bitmap>();
                                ArrayList<String> tempCodeList = new ArrayList<>();

                                try {
                                    for (String code : resultList) {
                                        java.net.URL url = new java.net.URL("http://141.164.40.63:8000/media/database/" + code + "/1.jpg");
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        InputStream input = connection.getInputStream();
                                        tempList.add(BitmapFactory.decodeStream(input));
                                        tempCodeList.add(code);
                                    }

                                    UserLike.setImages(tempList);
                                    UserLike.setCodes(tempCodeList);
                                    Message msg = likeOpenHandler.obtainMessage();
                                    likeOpenHandler.sendMessage(msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }.start();
            }
        });

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

        fabEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fabSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        fabMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FAB_FLAG) {
                    FAB_FLAG = false;
                    fabEditButton.setVisibility(View.INVISIBLE);
                    fabEditTextView.setVisibility(View.INVISIBLE);
                    fabSettingButton.setVisibility(View.INVISIBLE);
                    fabSettingTextView.setVisibility(View.INVISIBLE);
                    backBlurView.setVisibility(View.INVISIBLE);

                    fabMainButton.startAnimation(FABCloseAnimation);
                    fabSettingButton.startAnimation(fab_close);
                    fabEditButton.startAnimation(fab_close);
                } else {
                    FAB_FLAG = true;
                    fabEditButton.setVisibility(View.VISIBLE);
                    fabEditTextView.setVisibility(View.VISIBLE);
                    fabSettingButton.setVisibility(View.VISIBLE);
                    fabSettingTextView.setVisibility(View.VISIBLE);
                    backBlurView.setVisibility(View.VISIBLE);

                    fabMainButton.startAnimation(FABOpenAnimation);
                    fabSettingButton.startAnimation(fab_open);
                    fabEditButton.startAnimation(fab_open);
                }
            }
        });
    }

    public void init() {
        new Thread() {
            public void run() {
                String resultString = HttpPostData.POST("account/getInfo/", new String[]{"email"}, new String[]{userEmail});

                if (resultString.equals("-1")
                        || resultString.equals("SEND_FAIL")
                        || resultString.equals("NO_DATA_RECEIVED")) {
                    Message msg = failDialogHandler.obtainMessage();
                    failDialogHandler.sendMessage(msg);

                } else {
                    userName = resultString.split("&")[0];

                    Message msg = initHandler.obtainMessage();
                    initHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    Handler initHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            nameTextView.setText(userName);
            emailTextView.setText(userEmail);
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
    Handler noLikeOpenHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            Intent intent = new Intent(getApplicationContext(), NoLikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    };

    @SuppressLint("HandlerLeak")
    Handler failDialogHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PortfolioActivity.this);
            builder.setTitle("정보를 불러오지 못했습니다");

            builder.setPositiveButton("새로고침", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };

    public void onTitleClicked(View view) {

    }

    public void onBlurClicked(View view) {
        FAB_FLAG = false;
        fabEditButton.setVisibility(View.INVISIBLE);
        fabEditTextView.setVisibility(View.INVISIBLE);
        fabSettingButton.setVisibility(View.INVISIBLE);
        fabSettingTextView.setVisibility(View.INVISIBLE);
        backBlurView.setVisibility(View.INVISIBLE);

        fabMainButton.startAnimation(FABCloseAnimation);
        fabSettingButton.startAnimation(fab_close);
        fabEditButton.startAnimation(fab_close);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}