package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

public class AskActivity extends BaseActivity {

    // 데이터 전송용 배열
    private final String[] keys = new String[3];
    private final String[] data = new String[3];
    String result;

    // Elements
    private EditText emailEditText;
    private EditText titleEditText;
    private EditText contentEditText;
    private Button okButton;
    private TextView numTextView;

    private boolean insert1, insert2, insert3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        // 인스턴스화
        emailEditText = findViewById(R.id.Ask_EditText_email);
        titleEditText = findViewById(R.id.Ask_EditText_title);
        contentEditText = findViewById(R.id.Ask_EditText_content);
        okButton = findViewById(R.id.Ask_Button_done);
        numTextView = findViewById(R.id.Ask_TextView_textNum);

        // 초기화
        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        String savedUserEmail = preferences.getString("userEmail", "");
        emailEditText.setText(savedUserEmail);

        if (!emailEditText.getText().toString().equals("")) {
            insert1 = true;
        }

        // email EditText
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert1 = !emailEditText.getText().toString().equals("");

                if (insert1 && insert2 && insert3) {
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    okButton.setClickable(true);
                } else {
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                    okButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // title EditText
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert2 = !titleEditText.getText().toString().equals("");

                if (insert1 && insert2 && insert3) {
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    okButton.setClickable(true);
                } else {
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                    okButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // content EditText
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert3 = (contentEditText.getText().toString().length() > 0) && (contentEditText.getText().toString().length() <= 200);

                numTextView.setText("글자수 " + contentEditText.getText().toString().length() + "/200자");
                if (contentEditText.getText().toString().length() > 200) {
                    numTextView.setTextColor(Color.parseColor("#E50000"));
                } else {
                    numTextView.setTextColor(Color.BLACK);
                }

                if (insert1 && insert2 && insert3) {
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    okButton.setClickable(true);
                } else {
                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                    okButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // ok Button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                keys[0] = "email";
                keys[1] = "title";
                keys[2] = "content";
                data[0] = email;
                data[1] = title;
                data[2] = content;

                new Thread() {
                    public void run() {
                        result = HttpPostData.POST("account/ask/", keys, data);

                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }.start();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}