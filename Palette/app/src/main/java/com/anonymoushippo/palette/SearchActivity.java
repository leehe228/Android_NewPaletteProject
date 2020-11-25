package com.anonymoushippo.palette;

import android.content.SharedPreferences;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.*;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class SearchActivity extends BaseActivity {

    private final String[] keys = new String[1];
    private final String[] data = new String[1];
    private String result;

    ArrayList<String> adapterInfoList;
    ArrayList<String> adapterCodeList;
    ArrayList<String> infoList;
    ArrayList<String> codeList;
    private EditText searchEditText;
    private TextView warningTextView;

    private SearchAdapter resultAdapter;

    /* Loading View */
    ImageView loadingView;
    View backView;

    // STT
    SpeechRecognizer mRecognizer;
    Intent sttIntent;

    ListView listView;

    ImageButton ttsOrKillButton;
    boolean ttsOrKillFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageButton homeButton = findViewById(R.id.HomeButton);
        ImageButton popularButton = findViewById(R.id.PopularButton);
        ImageButton searchButton = findViewById(R.id.SearchButton);
        ImageButton likeButton = findViewById(R.id.LikeButton);
        ImageButton portfolioButton = findViewById(R.id.PortfolioButton);

        ttsOrKillButton = findViewById(R.id.Search_TTS);
        ttsOrKillFlag = false;

        searchEditText = findViewById(R.id.Search_EditText_search);

        adapterInfoList = new ArrayList<>();
        adapterCodeList = new ArrayList<>();
        infoList = new ArrayList<>();
        codeList = new ArrayList<>();

        listView = findViewById(R.id.SearchList_ListView);

        warningTextView = findViewById(R.id.Search_TextView_warning);

        // Adapter
        resultAdapter = new SearchAdapter(this, adapterInfoList);
        listView.setAdapter(resultAdapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), GalleryInformationActivity.class);
                intent.putExtra("CODE", adapterCodeList.get(i));
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        // STT
        sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        // Loading
        loadingView = findViewById(R.id.LoadingView);
        backView = findViewById(R.id.LoadingBack);
        GlideDrawableImageViewTarget loadingImage = new GlideDrawableImageViewTarget(loadingView);
        Glide.with(this).load(R.drawable.loading).into(loadingImage);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchEditText.getText().toString().length() == 0) {
                    warningTextView.setText("음성으로 검색하거나 검색어를 입력해주세요");

                    warningTextView.setVisibility(View.VISIBLE);
                } else {
                    warningTextView.setText("검색 결과가 없습니다");
                }

                adapterCodeList.clear();
                adapterInfoList.clear();
                if (searchEditText.getText().length() > 0) {
                    Search();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ttsOrKillFlag = searchEditText.getText().toString().length() != 0;

                if(ttsOrKillFlag){
                    ttsOrKillButton.setImageResource(R.drawable.kill);
                }
                else{
                    ttsOrKillButton.setImageResource(R.drawable.mic);

                    adapterCodeList.clear();
                    adapterInfoList.clear();
                    resultAdapter.notifyDataSetChanged();
                }
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

        ttsOrKillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsOrKillFlag) {
                    searchEditText.setText("");
                } else {
                    mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    mRecognizer.setRecognitionListener(listener);
                    mRecognizer.startListening(sttIntent);

                    loadingView.setVisibility(View.VISIBLE);
                }
            }
        });

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
                SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
                backView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.VISIBLE);
                keys[0] = "email";
                data[0] = preferences.getString("userEmail", "test@test.com");

                new Thread() {
                    public void run() {
                        String resultString = HttpPostData.POST("account/getLike/", keys, data);

                        if (resultString.equals("-1") || resultString.equals("SEND_FAIL")) {
                            //
                        } else {
                            if (resultString.equals("")) {
                                UserLike.setImages(null);
                                Message msg = noLikeOpenHandler.obtainMessage();
                                noLikeOpenHandler.sendMessage(msg);
                            } else {
                                String[] resultList = resultString.split("-");

                                ArrayList<Bitmap> tempList = new ArrayList<>();
                                ArrayList<String> tempCodeList = new ArrayList<>();

                                try {
                                    for (String code : resultList) {
                                        java.net.URL url = new java.net.URL("http://141.164.40.63:8000/media/database/" + code + "/1.jpg");
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        InputStream input = connection.getInputStream();
                                        tempList.add(BitmapFactory.decodeStream(input));
                                        tempCodeList.add(code);
                                    }

                                    UserLike.setImages(tempList);
                                    UserLike.setCodes(tempCodeList);
                                    Message msg = likeOpenHandler.obtainMessage();
                                    likeOpenHandler.sendMessage(msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }.start();
            }
        });

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private final RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @SuppressLint("ShowToast")
        @Override
        public void onError(int error) {
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                case SpeechRecognizer.ERROR_CLIENT:
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                case SpeechRecognizer.ERROR_NO_MATCH:
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                case SpeechRecognizer.ERROR_SERVER:
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                default:
                    searchEditText.setText("");
                    loadingView.setVisibility(View.INVISIBLE);
                    System.err.println(error);
                    break;
            }
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            loadingView.setVisibility(View.INVISIBLE);

            String resultKeyword = "";
            for (String match : matches) {
                resultKeyword = resultKeyword.concat(match);
            }

            if (resultKeyword.equals("")) {
                searchEditText.setText("");
            } else {
                searchEditText.setText(resultKeyword);
            }

            adapterCodeList.clear();
            adapterInfoList.clear();
            if (searchEditText.getText().length() > 0) {
                Search();
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

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
        loadingView.setVisibility(View.INVISIBLE);
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

    public int CheckSame(String str1, String str2) {
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

    @SuppressLint("HandlerLeak")
    Handler likeOpenHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            Intent intent = new Intent(getApplicationContext(), LikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    /* ListView Adapter 재설정 Handler */
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            resultAdapter.notifyDataSetChanged();
            warningTextView.setVisibility(View.INVISIBLE);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler noLikeOpenHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            Intent intent = new Intent(getApplicationContext(), NoLikeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    };

    /* ListView Adapter 결과 없음 */
    @SuppressLint("HandlerLeak")
    Handler noResultHandler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            resultAdapter.notifyDataSetChanged();
            warningTextView.setVisibility(View.VISIBLE);
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