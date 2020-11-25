package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NotNull;

public class EditInterestActivity extends BaseActivity {

    // 데이터 전송용 배열
    private final String[] keys = new String[2];
    private final String[] data = new String[2];
    String result;
    private String userEmail;

    // Bubble Picker
    BubblePicker bubblePicker;

    // Bubble Picker 카테고리
    String[] titles = {"사진전", "가구전시", "유아", "미디어 아트", "학생 작품", "제품 디자인", "캐릭터", "패션", "레고 전시"};

    // 저장용 배열
    String[] check = {"0", "0", "0", "0", "0", "0", "0", "0", "0"};

    // Bubble 색
    int[] colors = {Color.parseColor("#FFB5D8"), Color.parseColor("#A7E7FF"), Color.parseColor("#FFEEAC"), Color.parseColor("#CBFFD2"), Color.parseColor("#F8D5FF"),
            Color.parseColor("#FFB5D8"), Color.parseColor("#A7E7FF"), Color.parseColor("#FFEEAC"), Color.parseColor("#CBFFD2")};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interest);

        // 인스턴스화
        Button okButton = findViewById(R.id.EditInterest_Button_done);
        bubblePicker = findViewById(R.id.EditInterest_BubblePicker_main);

        // 초기화
        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        userEmail = preferences.getString("userEmail", "");

        // Bubble Picker Adapter
        bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return titles.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int i) {
                PickerItem pickerItem = new PickerItem();

                pickerItem.setTitle(titles[i]);
                pickerItem.setGradient(new BubbleGradient(colors[i], colors[i], BubbleGradient.HORIZONTAL));
                pickerItem.setTextColor(Color.BLACK);

                return pickerItem;
            }
        });

        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {
                String selectedItem = pickerItem.getTitle();

                for(int i = 0; i < titles.length; i++){
                    if(titles[i].equals(selectedItem)){
                        check[i] = "1";
                        break;
                    }
                }
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                String deselectedItem = pickerItem.getTitle();

                for(int i = 0; i < titles.length; i++){
                    if(titles[i].equals(deselectedItem)){
                        check[i] = "0";
                        break;
                    }
                }
            }
        });

        bubblePicker.setBubbleSize(3);
        bubblePicker.setMaxSelectedCount(3);
        bubblePicker.setCenterImmediately(true);

        // 완료 버튼
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keys[0] = "email";
                keys[1] = "interest";
                data[0] = userEmail;
                data[1] = check[0] + check[1] + check[2] + check[3] + check[4] + check[5] + check[6] + check[7] + check[8];

                new Thread() {
                    public void run() {
                        result = HttpPostData.POST("account/setInterest/", keys, data);

                        Log.i("result : ", result);

                        switch (result) {
                            case "1": {
                                Message msg = dialogHandler.obtainMessage();
                                dialogHandler.sendMessage(msg);
                                break;
                            }

                            case "-1":
                            case "SEND_FAIL":
                            case "NO_DATA_RECEIVED": {

                            }
                        }
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        bubblePicker.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        bubblePicker.onPause();
    }

    // 뒤로가기 버튼
    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressLint("HandlerLeak")
    Handler dialogHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditInterestActivity.this);
            builder.setTitle("저장했습니다");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };
}