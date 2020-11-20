package com.anonymoushippo.palette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class PrivatePrivacyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_privacy);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}