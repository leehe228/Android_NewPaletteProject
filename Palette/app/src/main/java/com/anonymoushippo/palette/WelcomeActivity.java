package com.anonymoushippo.palette;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 인스턴스화
        Button loginButton = findViewById(R.id.Welcome_Button_login);
        Button signUpButton = findViewById(R.id.Welcome_Button_signup);
        Button testButton = findViewById(R.id.Welcome_Button_test);

        // 로그인 버튼
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplicationContext(), LoginActivity.class);
                signUpIntent.putExtra("open", "TEST");
                startActivity(signUpIntent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        // 로그인 버튼
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplicationContext(), LoginActivity.class);
                signUpIntent.putExtra("open", "NONE");
                startActivity(signUpIntent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        // 회원가입 버튼
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUpIntent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    // 뒤로가기 버튼
    @Override
    public void onBackPressed() {
        finish();
    }
}