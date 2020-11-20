package com.anonymoushippo.palette;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class SearchActivity extends BaseActivity {

    ArrayList<String> adapterInfoList;
    ArrayList<String> adapterCodeList;
    ArrayList<String> infoList;
    ArrayList<String> codeList;
    private EditText searchEditText;

    private SearchAdapter resultAdapter;

    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageButton homeButton = findViewById(R.id.HomeButton);
        ImageButton popularButton = findViewById(R.id.PopularButton);
        ImageButton searchButton = findViewById(R.id.SearchButton);
        ImageButton likeButton = findViewById(R.id.LikeButton);
        ImageButton portfolioButton = findViewById(R.id.PortfolioButton);

        searchEditText = findViewById(R.id.Search_EditText_search);

        adapterInfoList = new ArrayList<>();
        adapterCodeList = new ArrayList<>();
        infoList = new ArrayList<>();
        codeList = new ArrayList<>();

        ListView listView = findViewById(R.id.SearchList_ListView);

        // Adapter
        resultAdapter = new SearchAdapter(this, adapterInfoList);
        listView.setAdapter(resultAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterCodeList.clear();
                adapterInfoList.clear();
                if (searchEditText.getText().length() > 0){
                    Search();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        new Thread() {
            public void run() {
                result = HttpPostData.DOWNLOAD_JSON("media/test/database.json");
                try {
                    PARSE_JSON(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        popularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopularActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LikeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void PARSE_JSON(String originalJSONString) throws JSONException {

        JSONObject jsonObject = new JSONObject(originalJSONString);

        JSONArray resultArray = jsonObject.getJSONArray("DATA");

        int ListLength = resultArray.length();

        for (int i = 0; i < ListLength; i++) {
            JSONObject innerObject = resultArray.getJSONObject(i);

            String code = innerObject.getString("CODE");
            String name = innerObject.getString("TITLE");
            String creator = innerObject.getString("CREATOR");
            String dueDay = innerObject.getString("DUEDATE");
            String category = innerObject.getString("CATEGORY");

            String[] titles = {"사진전", "가구전시", "유아", "미디어 아트", "학생 작품", "제품 디자인", "캐릭터", "패션", "레고 전시"};

            char[] cat = category.toCharArray();
            String catString = "";

            for (int j = 0; j < cat.length; j++) {
                if (cat[j] == '1') {
                    catString = catString.concat("#" + titles[j] + " ");
                }
            }
            catString = catString + " ";
            String dateTemp = "~" + dueDay.substring(0, 4) + "." + dueDay.substring(4, 6) + "." + dueDay.substring(6) + ".";
            System.out.println(code + "-" + name + "-" + creator + "-" + dateTemp + "-" + catString);
            infoList.add(code + "-" + name + "-" + creator + "-" + dateTemp + "-" + catString);
            codeList.add(code);
        }
        Message msg = parseFinishHandler.obtainMessage();
        parseFinishHandler.sendMessage(msg);
    }

    private void Search() {
        String keyword = searchEditText.getText().toString();
        int c = 0;

        for (int i = 0; i < infoList.size(); i++) {
            String name = infoList.get(i).split("-")[1];
            String creator = infoList.get(i).split("-")[2];

            if (keyword.length() != 0 && CheckSame(keyword, name + creator) > 1) {
                adapterInfoList.add(infoList.get(i));
                adapterCodeList.add(codeList.get(i));
                c++;
            }
        }

        if (c == 0) {
            Message msg = noResultHandler.obtainMessage();
            noResultHandler.sendMessage(msg);
        } else {
            Message msg = mHandler.obtainMessage();
            mHandler.sendMessage(msg);
        }

    }

    public int CheckSame(String str1, String str2){
        int c = 0;

        char[] str1Array = str1.replace(" ", "").toCharArray();
        char[] str2Array = str2.replace(" ", "").toCharArray();

        for (char item : str1Array) {
            for (char value : str2Array) {
                if (item == value) {
                    c++;
                }
            }
        }

        return c;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    /* ListView Adapter 재설정 Handler */
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            resultAdapter.notifyDataSetChanged();
        }
    };

    /* ListView Adapter 결과 없음 */
    @SuppressLint("HandlerLeak")
    Handler noResultHandler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            resultAdapter.notifyDataSetChanged();
        }
    };

    @SuppressLint("HandlerLeak")
    Handler parseFinishHandler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            Search();
        }
    };
}