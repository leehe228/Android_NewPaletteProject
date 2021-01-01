package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class SettingActivity extends BaseActivity {

    private final String[] keys = new String[1];
    private final String[] data = new String[1];

    /* Loading View */
    ImageView loadingView;
    View backView;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        loadingView = findViewById(R.id.LoadingView);
        backView = findViewById(R.id.LoadingBack);
        GlideDrawableImageViewTarget loadingImage = new GlideDrawableImageViewTarget(loadingView);
        Glide.with(this).load(R.drawable.loading).into(loadingImage);
        loadingView.setVisibility(View.INVISIBLE);

        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        userEmail = preferences.getString("userEmail", "test@test.com");

        // 인스턴스화
        // Button myProfileButton = findViewById(R.id.Setting_Button_MyProfile);
        Button editInterestButton = findViewById(R.id.Setting_Button_EditInterest);
        Button setTTSButton = findViewById(R.id.Setting_Button_SetTTS);
        Button developerButton = findViewById(R.id.Setting_Button_Developer);
        Button askButton = findViewById(R.id.Setting_Button_Ask);

        //Button changePasswordButton = findViewById(R.id.Setting_Button_ChangePassword);
        Button logoutButton = findViewById(R.id.Setting_Button_LogOut);
        Button quitButton = findViewById(R.id.Setting_Button_Quit);

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

        // Interest 수정
        editInterestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditInterestActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        setTTSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetTTSActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        // 개발팀
        developerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeveloperActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        // 문의사항
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AskActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        // 비밀번호 변경
        /*changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });*/

        // 로그아웃
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userEmail", "");
                editor.putBoolean("autoLogin", false);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        // 회원 탈퇴
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuitActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
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

}