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

public class SignUpActivity extends BaseActivity {

    // 데이터 전송용 배열
    private final String[] keys = new String[4];
    private final String[] data = new String[4];
    String result;

    // 경고 TextView
    TextView emailAlertTextView;
    TextView password1AlertTextView;
    TextView password2AlertTextView;
    TextView alertTextView;

    // EditText 입력 감지 Flag
    Boolean insert1, insert2, insert3, insert4, insert5;

    // 회원가입 단계
    int step = 0;

    // 확인 코드
    private String okCODE;

    // 위젯
    Button okButton;
    EditText emailEditText;
    EditText password1EditText;
    EditText password2EditText;
    EditText nameEditText;
    EditText ageEditText;
    ImageButton backButton;
    TextView emailTextView;
    TextView password1TextView;
    TextView password2TextView;
    TextView nameTextView;
    TextView ageTextView;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 인스턴스화
        okButton = findViewById(R.id.SignUp_Button_done);
        emailEditText = findViewById(R.id.SignUp_EditText_email);
        password1EditText = findViewById(R.id.SignUp_EditText_password1);
        password2EditText = findViewById(R.id.SignUp_EditText_password2);
        nameEditText = findViewById(R.id.SignUp_EditText_name);
        ageEditText = findViewById(R.id.SignUp_EditText_age);

        emailTextView = findViewById(R.id.SignUp_TextView_email);
        password1TextView = findViewById(R.id.SignUp_TextView_password1);
        password2TextView = findViewById(R.id.SignUp_TextView_password2);
        nameTextView = findViewById(R.id.SignUp_TextView_name);
        ageTextView = findViewById(R.id.SignUp_TextView_age);

        emailAlertTextView = findViewById(R.id.SignUp_TextView_alert1);
        password1AlertTextView = findViewById(R.id.SignUp_TextView_alert2);
        password2AlertTextView = findViewById(R.id.SignUp_TextView_alert3);
        alertTextView = findViewById(R.id.SignUp_TextView_alert4);

        Button privacyButton = findViewById(R.id.SignUp_Button_privacy);

        // 초기화
        insert1 = insert2 = insert3 = insert4 = insert5 = false;

        // 개인정보 처리방침 Button
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrivatePrivacyActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        // 회원가입 Button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (step == 0 && insert1 && insert2 && insert3 && insert4 && insert5) {
                    email = emailEditText.getText().toString();
                    String password;

                    try {
                        password = AES256Chiper.AES_Encode(password1EditText.getText().toString());
                    } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                        e.printStackTrace();
                        password = password1EditText.getText().toString();
                    }

                    String name = nameEditText.getText().toString();
                    String age = ageEditText.getText().toString();

                    keys[0] = "email";
                    keys[1] = "password";
                    keys[2] = "name";
                    keys[3] = "age";
                    data[0] = email;
                    data[1] = password;
                    data[2] = name;
                    data[3] = age;

                    final String[] tempKeys = {"email"};
                    final String[] tempData = {email};

                    Log.i("test", email);

                    okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                    okButton.setText("전송중");
                    okButton.setClickable(false);

                    new Thread() {
                        public void run() {
                            okCODE = HttpPostData.POST("account/sendCode/", tempKeys, tempData);
                            Log.i("OK CODE", okCODE);

                            if(okCODE.equals("-1") || okCODE.equals("SEND_FAIL")){
                                Message msg = failHandler.obtainMessage();
                                failHandler.sendMessage(msg);
                            } else{
                                Message msg = sendHandler.obtainMessage();
                                sendHandler.sendMessage(msg);

                                step = 1;
                            }

                        }
                    }.start();

                } else if (step == 1 && insert1) {
                    if (emailEditText.getText().toString().equals(okCODE)) {
                        new Thread() {
                            public void run() {
                                result = HttpPostData.POST("account/signup/", keys, data);

                                Log.i("result : ", result);

                                switch (result) {
                                    case "1": {
                                        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("userEmail", email);
                                        editor.apply();

                                        Intent intent = new Intent(getApplicationContext(), GetInterestActivity.class);
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
                                        break;
                                    }
                                }
                            }
                        }.start();
                    } else {
                        alertTextView.setText("확인코드가 일치하지 않습니다");
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);
                    }
                }
            }
        });

        // 이메일 EditText
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (step == 0) {
                    insert1 = !emailEditText.getText().toString().equals("") && android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches();

                    if (insert1) {
                        emailAlertTextView.setVisibility(View.INVISIBLE);
                    } else {
                        emailAlertTextView.setVisibility(View.VISIBLE);
                    }

                    if(insert1 && insert2 && insert3 && insert4 && insert5){
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                        okButton.setClickable(true);
                    }
                    else{
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                        okButton.setClickable(false);
                    }

                } else if (step == 1) {
                    insert1 = emailEditText.getText().toString().length() == 6;

                    if (insert1) {
                        emailAlertTextView.setVisibility(View.INVISIBLE);
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                        okButton.setClickable(true);
                    } else {
                        emailAlertTextView.setVisibility(View.VISIBLE);
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                        okButton.setClickable(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 비밀번호1 EditText
        password1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert2 = !password1EditText.getText().toString().equals("") && password1EditText.getText().toString().length() >= 8;

                if (insert2) {
                    password1AlertTextView.setVisibility(View.INVISIBLE);
                } else {
                    password1AlertTextView.setVisibility(View.VISIBLE);
                }

                if(step == 0){
                    if(insert1 && insert2 && insert3 && insert4 && insert5){
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                        okButton.setClickable(true);
                    }
                    else{
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                        okButton.setClickable(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        // 비밀번호2 EditText
        password2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert3 = password2EditText.getText().toString().equals(password1EditText.getText().toString());

                if (insert3) {
                    password2AlertTextView.setVisibility(View.INVISIBLE);
                } else {
                    password2AlertTextView.setVisibility(View.VISIBLE);
                }

                if(step == 0){
                    if(insert1 && insert2 && insert3 && insert4 && insert5){
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                        okButton.setClickable(true);
                    }
                    else{
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                        okButton.setClickable(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        // 이름 EditText
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert4 = !nameEditText.getText().toString().equals("");

                if(step == 0){
                    if(insert1 && insert2 && insert3 && insert4 && insert5){
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                        okButton.setClickable(true);
                    }
                    else{
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                        okButton.setClickable(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        // 나이 EditText
        ageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                insert5 = !ageEditText.getText().toString().equals("");

                if(step == 0){
                    if(insert1 && insert2 && insert3 && insert4 && insert5){
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                        okButton.setClickable(true);
                    }
                    else{
                        okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                        okButton.setClickable(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler failHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            // 확인 코드 모드로 변환
            okButton.setClickable(true);
            okButton.setText("재전송");
            okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
        }
    };

    @SuppressLint("HandlerLeak")
    Handler sendHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            // 확인 코드 모드로 변환
            okButton.setClickable(false);
            okButton.setText("확인");
            okButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));

            emailTextView.setText("확인 코드");
            emailEditText.setInputType(2);
            emailEditText.setText("");

            password1TextView.setVisibility(View.INVISIBLE);
            password1EditText.setVisibility(View.INVISIBLE);
            password2TextView.setVisibility(View.INVISIBLE);
            password2EditText.setVisibility(View.INVISIBLE);
            nameTextView.setVisibility(View.INVISIBLE);
            nameEditText.setVisibility(View.INVISIBLE);
            ageTextView.setVisibility(View.INVISIBLE);
            ageEditText.setVisibility(View.INVISIBLE);

            alertTextView.setVisibility(View.INVISIBLE);
            emailAlertTextView.setText("6자리 숫자를 입력해주세요");
        }
    };

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            alertTextView.setVisibility(View.VISIBLE);
        }
    };

    // 뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}