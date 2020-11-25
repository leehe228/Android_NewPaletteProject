package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class SetTTSActivity extends BaseActivity {

    // TTS instance
    private TextToSpeech tts;
    private TextView sampleEditText;

    // TTS State
    private boolean TTS_FLAG;
    private int TTS_Pitch;
    private int TTS_Speed;

    private Button listenButton;

    private final Bundle params = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tts);

        // 인스턴스화
        Button okButton = findViewById(R.id.SetTTS_Button_done);
        listenButton = findViewById(R.id.SetTTS_Button_listen);
        sampleEditText = findViewById(R.id.SetTTS_EditText_sample);

        SeekBar pitchSeekBar = findViewById(R.id.SetTTS_SeekBar_Pitch);
        SeekBar speedSeekBar = findViewById(R.id.SetTTS_SeekBar_Speed);

        // 초기화
        TTS_FLAG = false;
        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        TTS_Pitch = preferences.getInt("TTS_Pitch", 5);
        TTS_Speed = preferences.getInt("TTS_Speed", 5);

        pitchSeekBar.setProgress(TTS_Pitch);
        speedSeekBar.setProgress(TTS_Speed);

        initTTS();

        pitchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TTS_Pitch = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TTS_Speed = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 확인 버튼
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.stop();
                tts.shutdown();
                finish();

                SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("TTS_Pitch", TTS_Pitch - 1);
                editor.putInt("TTS_Speed", TTS_Speed - 1);
                editor.apply();
            }
        });

        // 듣기 버튼
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 재생
                if(!TTS_FLAG){
                    TTS_FLAG = true;
                    listenButton.setText("중지");
                    listenButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_darkgray));
                    Speech();
                }
                // 중지
                else{
                    TTS_FLAG = false;
                    listenButton.setText("들어보기");
                    listenButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    if(tts != null){
                        tts.stop();
                    }
                }

            }
        });
    }

    public void initTTS(){
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int language = tts.setLanguage(Locale.KOREAN);  // 언어 설정
                    //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                    if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        listenButton.setEnabled(true);
                        listenButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    }
                }
                Log.i("status", String.valueOf(status));
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                System.out.println("START");
            }

            @Override
            public void onDone(String utteranceId) {
                System.out.println("DONE");
                Message msg = speechDoneHandler.obtainMessage();
                speechDoneHandler.sendMessage(msg);

            }

            @Override
            public void onError(String utteranceId) {
                System.out.println("ERROR");
            }
        });

    }

    private void Speech() {
        String text = sampleEditText.getText().toString().trim();

        float ttsPitchF = TTS_Pitch / 5.0f;
        float ttsSpeedF = TTS_Speed / 5.0f;

        tts.setPitch(ttsPitchF);
        tts.setSpeechRate(ttsSpeedF);

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, text);
    }

    @Override
    public void onBackPressed(){
        tts.stop();
        tts.shutdown();
        finish();
    }

    @SuppressLint("HandlerLeak")
    android.os.Handler speechDoneHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            listenButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
            listenButton.setText("들어보기");
            TTS_FLAG = false;
            tts.stop();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(tts != null){
            tts.stop();
        }
    }
}