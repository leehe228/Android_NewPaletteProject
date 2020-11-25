package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class QuitActivity extends BaseActivity {

    private boolean insert1, insert2;

    private TextView alertTextView;

    // 데이터 전송용 배열
    private final String[] keys = new String[2];
    private final String[] data = new String[2];
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit);

        // 인스턴스화
        final EditText emailEditText = findViewById(R.id.Quit_EditText_email);
        final EditText passwordEditText = findViewById(R.id.Quit_EditText_password);
        final Button okButton = findViewById(R.id.Quit_Button_done);

        alertTextView = findViewById(R.id.Quit_TextView_alert);

        // 초기화
        insert1 = insert2 = false;

        // email EditText
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert1 = !emailEditText.getText().toString().equals("");

                if(insert1 && insert2){
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    okButton.setClickable(true);
                }
                else{
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                    okButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // password EditText
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert2 = !passwordEditText.getText().toString().equals("");

                if(insert1 && insert2){
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    okButton.setClickable(true);
                }
                else{
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                    okButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 확인 버튼
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password;

                try {
                    password = AES256Chiper.AES_Encode(passwordEditText.getText().toString());
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
                        | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                    password = passwordEditText.getText().toString();
                }

                keys[0] = "email"; keys[1] = "password";
                data[0] = email; data[1] = password;

                new Thread() {
                    public void run() {
                        result = HttpPostData.POST("account/deleteAccount/", keys, data);

                        Log.i("result", result);

                        switch (result) {
                            case "1":{
                                SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("userEmail", "");
                                editor.putBoolean("autoLogin", false);
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                                break;
                            }

                            case "-1":
                            case "SEND_FAIL":
                            case "NO_DATA_RECEIVED": {
                                Message msg = mHandler.obtainMessage();
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                }.start();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            alertTextView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}