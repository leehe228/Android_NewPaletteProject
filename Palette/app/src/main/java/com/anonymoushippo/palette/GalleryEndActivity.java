package com.anonymoushippo.palette;

import android.content.Intent;
import android.os.Bundle;

public class GalleryEndActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_end);

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}