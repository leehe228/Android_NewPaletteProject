package com.anonymoushippo.palette;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionActivity extends BaseActivity {

    private int PERMISSION_CHECK_INTERNET, PERMISSION_CHECK_RECORD, PERMISSION_CHECK_ACCESS, PERMISSION_CHECK_CAMERA, PERMISSION_CHECK_STORAGE;
    private final int PERMISSION_REQUEST_INTERNET = 1001;
    private final int PERMISSION_REQUEST_RECORD = 1002;
    private final int PERMISSION_REQUEST_ACCESS = 1003;
    private final int PERMISSION_REQUEST_CAMERA = 1004;
    private final int PERMISSION_REQUEST_STORAGE = 1005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        Button permissionButton = findViewById(R.id.Permission_Button_re);
        PERMISSION_CHECK_INTERNET = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        PERMISSION_CHECK_RECORD = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        PERMISSION_CHECK_ACCESS = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        PERMISSION_CHECK_CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        //PERMISSION_CHECK_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PERMISSION_CHECK_INTERNET != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_INTERNET);
                }

                if (PERMISSION_CHECK_RECORD != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD);
                }

                if (PERMISSION_CHECK_ACCESS != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_ACCESS);
                }

                if (PERMISSION_CHECK_CAMERA != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                }

                /*if (PERMISSION_CHECK_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                }*/

                PERMISSION_CHECK_INTERNET = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
                PERMISSION_CHECK_RECORD = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
                PERMISSION_CHECK_ACCESS = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE);
                PERMISSION_CHECK_CAMERA = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                //PERMISSION_CHECK_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });

        new Thread() {
            public void run() {
                do {
                    PERMISSION_CHECK_INTERNET = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
                    PERMISSION_CHECK_RECORD = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
                    PERMISSION_CHECK_ACCESS = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE);
                    PERMISSION_CHECK_CAMERA = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                    //PERMISSION_CHECK_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } while (PERMISSION_CHECK_RECORD != PackageManager.PERMISSION_GRANTED
                        || PERMISSION_CHECK_INTERNET != PackageManager.PERMISSION_GRANTED
                        || PERMISSION_CHECK_ACCESS != PackageManager.PERMISSION_GRANTED
                        || PERMISSION_CHECK_CAMERA != PackageManager.PERMISSION_GRANTED);

                Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                startActivity(intent);
                finish();

            }
        }.start();
    }
}