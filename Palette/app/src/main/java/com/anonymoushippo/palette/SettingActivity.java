package com.anonymoushippo.palette;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 인스턴스화
        // Button myProfileButton = findViewById(R.id.Setting_Button_MyProfile);
        Button editInterestButton = findViewById(R.id.Setting_Button_EditInterest);
        Button setTTSButton = findViewById(R.id.Setting_Button_SetTTS);
        Button developerButton = findViewById(R.id.Setting_Button_Developer);
        Button askButton = findViewById(R.id.Setting_Button_Ask);

        //Button changePasswordButton = findViewById(R.id.Setting_Button_ChangePassword);
        Button logoutButton = findViewById(R.id.Setting_Button_LogOut);
        Button quitButton = findViewById(R.id.Setting_Button_Quit);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}