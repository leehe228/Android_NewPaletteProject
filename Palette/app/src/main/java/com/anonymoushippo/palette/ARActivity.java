package com.anonymoushippo.palette;

import android.content.Intent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class ARActivity extends BaseActivity {

    String CODE, INDEX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        Intent intent = getIntent();
        CODE = intent.getStringExtra("CODE");
        INDEX = intent.getStringExtra("INDEX");

    }

    @Override
    public void onBackPressed(){
        finish();
    }
}