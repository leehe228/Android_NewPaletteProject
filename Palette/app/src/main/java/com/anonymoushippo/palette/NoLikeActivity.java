package com.anonymoushippo.palette;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class NoLikeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_like);

        ImageButton homeButton = findViewById(R.id.HomeButton);
        ImageButton popularButton = findViewById(R.id.PopularButton);
        ImageButton searchButton = findViewById(R.id.SearchButton);
        ImageButton likeButton = findViewById(R.id.LikeButton);
        ImageButton portfolioButton = findViewById(R.id.PortfolioButton);

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

    @Override
    public void onBackPressed() {
        finish();
    }

}