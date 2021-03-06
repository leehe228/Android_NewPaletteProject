package com.anonymoushippo.palette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.os.Bundle;
import android.widget.ImageButton;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LikeActivity extends BaseActivity {

    BubblePicker bubblePicker;

    ArrayList<Bitmap> images;
    ArrayList<Drawable> drawables;
    ArrayList<String> codes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        images = UserLike.getImages();
        codes = UserLike.getCodes();
        drawables = new ArrayList<>();

        bubblePicker = findViewById(R.id.GetInterest_BubblePicker_main);

        bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return images.size();
            }

            @NotNull
            @Override
            public PickerItem getItem(int i) {
                PickerItem item = new PickerItem();
                Drawable temp = new BitmapDrawable(images.get(i));
                item.setIcon(temp);
                drawables.add(temp);
                return item;
            }
        });

        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {
                Drawable selectedItem = pickerItem.getIcon();

                for (int i = 0; i < drawables.size(); i++) {
                    assert selectedItem != null;
                    if (selectedItem.equals(drawables.get(i))) {
                        Intent intent = new Intent(getApplicationContext(), GalleryInformationActivity.class);
                        intent.putExtra("CODE", codes.get(i));
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    }
                }
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                String deselectedItem = pickerItem.getTitle();
            }
        });

        bubblePicker.setBubbleSize(12);
        bubblePicker.setMaxSelectedCount(1);
        bubblePicker.setCenterImmediately(true);

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
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        bubblePicker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bubblePicker.onPause();
    }
}