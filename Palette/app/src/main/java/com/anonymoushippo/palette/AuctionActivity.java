package com.anonymoushippo.palette;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

public class AuctionActivity extends BaseActivity {

    private String result;
    String[] keys = new String[1];
    String[] data = new String[1];

    private ImageView mainView;
    private Button dateView;

    private String galleryCode, auctionCode, artNumber, threeArtNumber;

    private String startPrice, highPrice, title, creator;
    private TextView titleTextView, startPriceTextView, highPriceTextView;
    private String enterPrice;
    private String lastBuyerEmail;

    private EditText priceEditText;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);

        // 초기화
        Intent intent = getIntent();
        galleryCode = intent.getStringExtra("CODE");
        artNumber = intent.getStringExtra("NUMBER");

        switch (artNumber.length()) {
            case 1: {
                threeArtNumber = "00" + artNumber;
                break;
            }
            case 2: {
                threeArtNumber = "0" + artNumber;
                break;
            }
            case 3: {
                threeArtNumber = artNumber;
                break;
            }
        }
        auctionCode = galleryCode + threeArtNumber;

        init();

        // 인스턴스화
        mainView = findViewById(R.id.Auction_ImageView);
        dateView = findViewById(R.id.Auction_Button_onProcess);
        priceEditText = findViewById(R.id.Auction_EditText_price);
        payButton = findViewById(R.id.Auction_Button_pay);

        titleTextView = findViewById(R.id.Auction_title);
        startPriceTextView = findViewById(R.id.Auction_TextView_startPrice);
        highPriceTextView = findViewById(R.id.Auction_TextView_highPrice);

        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (priceEditText.length() == 0) {
                    payButton.setText("입찰");
                } else {
                    int price = Integer.parseInt(priceEditText.getText().toString()) * 1000;
                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    payButton.setText(myFormatter.format(price) + "원 입찰");
                }

                if (Integer.parseInt(priceEditText.getText().toString()) * 1000 > Integer.parseInt(highPrice)) {
                    payButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button));
                    payButton.setEnabled(true);
                } else {
                    payButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.basic_button_unclick));
                    payButton.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = dialogHandler.obtainMessage();
                dialogHandler.sendMessage(msg);
            }
        });

    }

    public void init() {
        keys[0] = "auctionCode";
        data[0] = auctionCode;

        new Thread() {
            public void run() {
                result = HttpPostData.POST("gallery/getAuction/", keys, data);

                Log.e("RESULT", result);

                /* 이메일-시작가-최고가-시작일-종료일-구매자이메일*/
                String[] resultString = result.split("&");

                startPrice = resultString[1];
                highPrice = resultString[2];
                lastBuyerEmail = resultString[5];

                Message msg = initHandler.obtainMessage();
                initHandler.sendMessage(msg);

            }
        }.start();
    }

    public void update() {
        SharedPreferences preferences = getSharedPreferences("com.AnonymousHippo.Palette.sharePreference", MODE_PRIVATE);
        String userEmail = "test@test.com";

        String[] aKeys = new String[3];
        String[] aData = new String[3];

        aKeys[0] = "auctionCode";
        aKeys[1] = "newPrice";
        aKeys[2] = "buyerEmail";

        aData[0] = auctionCode;
        aData[1] = String.valueOf(Integer.parseInt(priceEditText.getText().toString()) * 1000);
        aData[2] = userEmail;

        new Thread() {
            public void run() {
                result = HttpPostData.POST("gallery/auctionProcess/", aKeys, aData);

                init();
            }
        }.start();

    }

    @SuppressLint("HandlerLeak")
    Handler initHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            Glide.with(getApplicationContext()).load("http://141.164.40.63:8000/media/database/" + galleryCode + "/" + artNumber + ".jpg").into(mainView);
            titleTextView.setText(title);
            startPriceTextView.setText("경매 시작가 : " + startPrice);
            highPriceTextView.setText("경매 최고가 : " + highPrice);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler dialogHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AuctionActivity.this);
            builder.setTitle(priceEditText.getText() + ",000원을 입찰하시겠습니까?");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    update();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };

    @SuppressLint("HandlerLeak")
    Handler CanNotDialogHandler = new Handler() {
        @Override
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AuctionActivity.this);
            builder.setTitle("연속 입찰은 불가능합니다");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };
}