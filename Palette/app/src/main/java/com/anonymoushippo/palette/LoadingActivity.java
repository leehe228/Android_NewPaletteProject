package com.anonymoushippo.palette;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public class LoadingActivity extends BaseActivity {

    private int PERMISSION_CHECK_INTERNET, PERMISSION_CHECK_RECORD, PERMISSION_CHECK_ACCESS, PERMISSION_CHECK_CAMERA, PERMISSION_CHECK_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        /* 로고 애니메이션 */
        Animation LogoAnimation = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.translate);
        final TextView logoTextView = findViewById(R.id.Loading_TextView_title);
        logoTextView.startAnimation(LogoAnimation);

        // 권한 체크
        PERMISSION_CHECK_INTERNET = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        PERMISSION_CHECK_RECORD = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        PERMISSION_CHECK_ACCESS = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        // Open Check
        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        final boolean autoLoginFlag = preferences.getBoolean("autoLogin", false);

        final boolean INTERNET_STATUS = NetworkStatus.getStatus(getApplicationContext());

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                // PASSING
                Intent intent;
                if(PERMISSION_CHECK_RECORD == PackageManager.PERMISSION_GRANTED
                        && PERMISSION_CHECK_INTERNET == PackageManager.PERMISSION_GRANTED
                        && PERMISSION_CHECK_ACCESS == PackageManager.PERMISSION_GRANTED) {

                    if (INTERNET_STATUS){
                        if(autoLoginFlag){
                            intent = new Intent(getApplicationContext(), HomeActivity.class);
                        }
                        else{
                            intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        }
                    }
                    else{
                        intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                    }
                } else{
                    intent = new Intent(getApplicationContext(), PermissionActivity.class);
                }

                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        }, 1000);
    }
}