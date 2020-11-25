package com.anonymoushippo.palette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DeveloperActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}