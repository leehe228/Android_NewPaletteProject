package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.github.mmin18.widget.RealtimeBlurView;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class GalleryActivity extends BaseActivity implements GestureDetector.OnGestureListener {

    private String result;
    private String userEmail;
    String[] keys = new String[2];
    String[] data = new String[2];

    // 제스처
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector gestureScanner;

    /* 몰입모드 */
    private View decorView;
    private int uiOption;

    // 저장한 전시회 Flag
    private boolean SAVED;

    // 화면 회전 FLAG
    private boolean SCREEN_ROTATION;

    // 정보 표시 FLAG
    private boolean INFO_FLAG;
    private boolean PLAY_FLAG;
    private boolean AUTO_FLAG;
    private boolean REMOTE_FLAG;
    private boolean BGM_PLAY_FLAG;

    //
    private RealtimeBlurView blurView;
    private LinearLayout infoView;
    private TextView titleTextView;
    private TextView contentTextView;

    // 애니메이션
    private Animation slideUpAnimation;
    private Animation fade_in, fade_out;

    // 이미지 List
    private ArrayList<Bitmap> IMAGES;
    private String[] TITLES;
    private String[] CONTENTS;

    // 이미지 index
    private int INDEX, MAX_INDEX;

    // Main Image View
    private ImageView mainImageView;

    // 건너뛰기 버튼
    private Handler leftHandler, rightHandler;

    LinearLayout remoteController;
    LinearLayout remoteController2;

    // TTS
    private String nowToRead;

    // BGM
    private static MediaPlayer mediaPlayer;
    private ImageButton bgmButton;
    private ImageButton autoButton;

    // TTS instance
    private TextToSpeech tts;

    // TTS State
    private boolean TTS_FLAG;
    private int TTS_Pitch;
    private int TTS_Speed;
    private final Bundle params = new Bundle();
    private ImageButton infoButton;

    private InputMethodManager imm;

    ConstraintLayout mainLayout;

    private Animation slideLeft;
    private Animation slideRight;

    private ArrayList<String> resultList;

    private ImageButton plusButton;

    private ProgressBar progressBar;

    private String CREATOR;
    private String CATEGORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));

        /* 화면 꺼짐 방지 */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_gallery);

        /* 몰입 모드 (하단 소프트바 숨기기) */
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // 인스턴스화
        resultList = new ArrayList<>();

        mainLayout = findViewById(R.id.GalleryLayout);

        gestureScanner = new GestureDetector(this);

        mainImageView = findViewById(R.id.Gallery_ImageView_main);

        ImageButton leftButton = findViewById(R.id.Gallery_ImageButton_left);
        ImageButton rightButton = findViewById(R.id.Gallery_ImageButton_right);
        ImageButton closeButton = findViewById(R.id.Gallery_ImageButton_close);
        infoButton = findViewById(R.id.Gallery_ImageButton_info);
        plusButton = findViewById(R.id.Gallery_ImageButton_plus);

        blurView = findViewById(R.id.Gallery_blurView);
        infoView = findViewById(R.id.Gallery_LinearLayout_info);

        remoteController = findViewById(R.id.Gallery_RemoteController);
        remoteController2 = findViewById(R.id.Gallery_RemoteController2);

        bgmButton = findViewById(R.id.Gallery_ImageButton_BGM);
        autoButton = findViewById(R.id.Gallery_ImageButton_Auto);

        titleTextView = findViewById(R.id.Gallery_TextView_title);
        contentTextView = findViewById(R.id.Gallery_TextView_content);

        Button auctionButton = findViewById(R.id.Gallery_Button_Auction);

        progressBar = findViewById(R.id.Gallery_Progressbar);
        progressBar.setMax(100);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(null, 0);

        // 초기화
        SAVED = false;
        INFO_FLAG = false;
        PLAY_FLAG = false;
        AUTO_FLAG = false;
        REMOTE_FLAG = false;
        BGM_PLAY_FLAG = false;

        // 인덱스 세팅
        INDEX = 0;

        // 애니메이션
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        slideLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
        slideRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);

        // 빠른 전환 딜레이 (Milli Sec)
        final int FAST_CHANGE_DELAY = 150;

        // 갤러리 받아오기
        Intent intent = getIntent();
        final String CODE = intent.getStringExtra("CODE");
        TITLES = intent.getStringExtra("TITLES").split("-");
        CONTENTS = intent.getStringExtra("CONTENTS").split("-");
        String tempValue = intent.getStringExtra("NUMBER");
        CREATOR = intent.getStringExtra("CREATOR");
        CATEGORY = intent.getStringExtra("CATEGORY");
        if(tempValue == null){
            MAX_INDEX = 0;
        }
        else{
            MAX_INDEX = Integer.parseInt(tempValue);
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.bgm1_repeat);
        mediaPlayer.setLooping(true);

        // 사진
        IMAGES = new ArrayList<>();

        new Thread() {
            public void run() {
                result = HttpPostData.POST("account/getLike/", keys, data);

                if (result.equals("") || result.equals("-1") || result.equals("SEND_FAIL")) {

                }
                else {
                    Collections.addAll(resultList, result.split("-"));

                    for (String s : resultList) {
                        if (s.equals(CODE)) {
                            SAVED = true;
                            Message msg = initLikeHandler.obtainMessage();
                            initLikeHandler.sendMessage(msg);
                            break;
                        }
                    }
                }
            }
        }.start();

        // TTS
        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        TTS_Pitch = preferences.getInt("TTS_Pitch", 5);
        TTS_Speed = preferences.getInt("TTS_Speed", 5);

        userEmail = "test@test.com"; //preferences.getString("userEmail", "");

        // 데이터 받아오기
        keys[0] = "email";
        data[0] = userEmail;

        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int language = tts.setLanguage(Locale.KOREAN);  // 언어 설정
                    //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                    if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        infoButton.setEnabled(false);
                    } else {
                        infoButton.setEnabled(true);
                    }
                }

                float ttsPitchF = 1.0f;
                float ttsSpeedF = 1.0f;

                tts.setPitch(ttsPitchF);
                tts.setSpeechRate(ttsSpeedF);
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                TTS_FLAG = false;
                Message msg = speechDoneHandler.obtainMessage();
                speechDoneHandler.sendMessage(msg);
                tts.stop();

                if(AUTO_FLAG){
                    INDEX++;
                    Message msg2 = mHandler.obtainMessage();
                    mHandler.sendMessage(msg2);
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        // 조회수 올리기
        final String finalCODE = CODE;
        new Thread() {
            public void run() {
                String[] k = {"code"};
                String[] v = {finalCODE};
                String result = HttpPostData.POST("gallery/addView/", k, v);
                Log.i("addView", result);
            }
        }.start();

        // 데이터 받아오기
        new Thread() {
            public void run() {
                try {
                    for(int i = 1; i <= MAX_INDEX; i++) {
                        java.net.URL url = new java.net.URL("http://141.164.40.63:8000/media/database/" + finalCODE + "/" + i + ".jpg");
                        Log.i("i", String.valueOf(i));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        IMAGES.add(BitmapFactory.decodeStream(input));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
            }
        }.start();

        // Left Button
        leftButton.setOnTouchListener(new View.OnTouchListener() {

            private Handler leftHandler;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (leftHandler != null)
                            return true;
                        leftHandler = new Handler();
                        leftHandler.postDelayed(leftAction, FAST_CHANGE_DELAY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (leftHandler == null) return true;
                        leftHandler.removeCallbacks(leftAction);
                        leftHandler = null;
                        break;
                }
                return false;
            }

            final Runnable leftAction = new Runnable() {
                @Override
                public void run() {

                    if (INDEX > 0) {
                        INDEX--;
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);
                        leftHandler.postDelayed(this, FAST_CHANGE_DELAY);
                    }
                }
            };
        });

        // Right Button
        rightButton.setOnTouchListener(new View.OnTouchListener() {

            private Handler rightHandler;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rightHandler != null)
                            return true;
                        rightHandler = new Handler();
                        rightHandler.postDelayed(rightAction, FAST_CHANGE_DELAY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (rightHandler == null) return true;
                        rightHandler.removeCallbacks(rightAction);
                        rightHandler = null;
                        break;
                }
                return false;
            }

            final Runnable rightAction = new Runnable() {
                @Override
                public void run() {

                    if (INDEX < MAX_INDEX - 1) {
                        INDEX++;
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);
                        rightHandler.postDelayed(this, FAST_CHANGE_DELAY);
                    }
                }
            };
        });

        // BGM 플레이
        bgmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BGM_PLAY_FLAG) {
                    bgmButton.setImageResource(R.drawable.bgm_off);
                    mediaPlayer.pause();
                    BGM_PLAY_FLAG = false;
                } else {
                    bgmButton.setImageResource(R.drawable.bgm_on);
                    mediaPlayer.start();
                    BGM_PLAY_FLAG = true;
                }
            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AUTO_FLAG) {
                    autoButton.setImageResource(R.drawable.auto_off);
                    AUTO_FLAG = false;
                    infoButton.setImageResource(R.drawable.start);
                    PLAY_FLAG = false;
                    tts.stop();

                } else {
                    autoButton.setImageResource(R.drawable.auto_on);
                    AUTO_FLAG = true;
                    infoButton.setImageResource(R.drawable.pause);
                    PLAY_FLAG = true;
                    Speech();
                }
            }
        });

        // Close Button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BGM Kill
                mediaPlayer.stop();
                tts.stop();
                tts.shutdown();

                Intent intent = new Intent(getApplicationContext(), GalleryEndActivity.class);
                intent.putExtra("CREATOR", CREATOR);
                intent.putExtra("CATEGORY", CATEGORY);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        // Plus Button
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 좋아요 취소
                if (SAVED) {
                    plusButton.setImageResource(R.drawable.plus);
                    SAVED = false;

                    new Thread() {
                        public void run() {
                            keys[1] = "userLike";

                            StringBuilder stringBuilder = new StringBuilder();
                            // resultList.add(CODE);
                            for(String string : resultList){
                                if(!string.equals(CODE)){
                                    if(stringBuilder.length() > 0){
                                        stringBuilder.append("-");
                                    }
                                    stringBuilder.append(string);
                                }
                            }
                            data[1] = stringBuilder.toString();
                            HttpPostData.POST("account/setLike/", keys, data);
                        }
                    }.start();

                } else {
                    // 좋아요 추가됨
                    plusButton.setImageResource(R.drawable.checked);
                    SAVED = true;

                    new Thread() {
                        public void run() {
                            keys[1] = "userLike";

                            StringBuilder stringBuilder = new StringBuilder();
                            resultList.add(CODE);
                            for(String string : resultList){
                                if(stringBuilder.length() > 0){
                                    stringBuilder.append("-");
                                }
                                stringBuilder.append(string);
                            }
                            data[1] = stringBuilder.toString();
                            HttpPostData.POST("account/setLike/", keys, data);
                        }
                    }.start();
                }
            }
        });

        // 재생 button
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PLAY_FLAG) {
                    infoButton.setImageResource(R.drawable.start);
                    PLAY_FLAG = false;
                    tts.stop();
                } else {
                    Speech();
                }
            }
        });

        auctionButton.setOnClickListener(v -> {
            Intent aIntent = new Intent(getApplicationContext(), AuctionActivity.class);
            startActivity(aIntent);
            overridePendingTransition(0, 0);
        });
    }

    private void Speech() {
        infoButton.setImageResource(R.drawable.pause);
        PLAY_FLAG = true;

        String text = "이 작품의 제목은 " + TITLES[INDEX].trim() + "입니다.";
        text = text.concat(CONTENTS[INDEX].trim());
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, text);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("onConfigurationChanged", "onConfigurationChanged");

        // 세로 전환 시
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("onConfigurationChanged", "Configuration.ORIENTATION_PORTRAIT");
            SCREEN_ROTATION = false;
        }

        // 가로 전환 시
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("onConfigurationChanged", "Configuration.ORIENTATION_LANDSCAPE");
            decorView.setSystemUiVisibility(uiOption);
            SCREEN_ROTATION = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if(!REMOTE_FLAG){
            REMOTE_FLAG = true;
            Message msg = RCOnHandler.obtainMessage();
            RCOnHandler.sendMessage(msg);
        }
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        tts.stop();
        tts.shutdown();
        Intent intent = new Intent(getApplicationContext(), GalleryEndActivity.class);
        intent.putExtra("CREATOR", CREATOR);
        intent.putExtra("CATEGORY", CATEGORY);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onBlurClicked(View view) {
        INFO_FLAG = false;
        infoView.setAlpha(0f);
        infoView.setVisibility(View.INVISIBLE);
        blurView.setVisibility(View.INVISIBLE);
    }

    // Background -> Foreground 올라왔을 때 몰입 모드 재적용
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            decorView.setSystemUiVisibility(uiOption);
        } else{
            mediaPlayer.stop();
            bgmButton.setImageResource(R.drawable.start);
            PLAY_FLAG = false;
            tts.stop();
            tts.shutdown();
            TTS_FLAG = false;
        }
    }

    @SuppressLint("HandlerLeak")
    android.os.Handler initLikeHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            plusButton.setImageResource(R.drawable.checked);
        }
    };

    // 사진 변경 핸들러
    @SuppressLint("HandlerLeak")
    android.os.Handler mHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            mainImageView.setImageBitmap(IMAGES.get(INDEX));
            titleTextView.setText(TITLES[INDEX]);
            contentTextView.setText(CONTENTS[INDEX]);
            infoButton.setImageResource(R.drawable.start);
            PLAY_FLAG = false;
            tts.stop();

            if(AUTO_FLAG) {
                Speech();
            }

            progressBar.setProgress((int)(((double)INDEX / (double)(MAX_INDEX - 1)) * 100));
        }
    };

    @SuppressLint("HandlerLeak")
    android.os.Handler speechDoneHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            infoButton.setImageResource(R.drawable.start);
            PLAY_FLAG = false;
            tts.stop();
        }
    };

    // 리모컨 온
    @SuppressLint("HandlerLeak")
    android.os.Handler RCOnHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            remoteController.startAnimation(fade_in);
            remoteController2.startAnimation(fade_in);
            remoteController.setVisibility(View.VISIBLE);
            remoteController2.setVisibility(View.VISIBLE);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()  {
                public void run() {
                    remoteController.startAnimation(fade_out);
                    remoteController2.startAnimation(fade_out);
                    remoteController.setVisibility(View.INVISIBLE);
                    remoteController2.setVisibility(View.INVISIBLE);
                    REMOTE_FLAG = false;
                }
            }, 5000); // 0.5초후
        }
    };

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();

                if (INDEX == MAX_INDEX - 1) {
                    Intent intent = new Intent(getApplicationContext(), GalleryEndActivity.class);
                    intent.putExtra("CREATOR", CREATOR);
                    intent.putExtra("CATEGORY", CATEGORY);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
                if (INDEX < MAX_INDEX - 1) {
                    INDEX++;
                    mainImageView.startAnimation(slideLeft);
                }

                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
            }
            // left to right swipe
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();

                if (INDEX == 0) {
                    Toast.makeText(getApplicationContext(), "첫 번째 작품입니다", Toast.LENGTH_SHORT).show();
                }
                if (INDEX > 0) {
                    INDEX--;
                    mainImageView.startAnimation(slideRight);
                }

                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
            }
            // down to up swipe
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();

                infoView.setAlpha(1.0f);
                infoView.startAnimation(slideUpAnimation);
                infoView.setVisibility(View.VISIBLE);
                blurView.setVisibility(View.VISIBLE);

                INFO_FLAG = true;
            }
            // up to down swipe
            if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                //Toast.makeText(getApplicationContext(), "Swipe down", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ignored) {

        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(tts != null){
            tts.stop();
        }
    }
}