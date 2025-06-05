package com.sinhvien.quanlitruyen;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        ImageView imageView = findViewById(R.id.imageViewDetail);
        String imagePath = getIntent().getStringExtra("image_path");

        if (imagePath != null) {
            Glide.with(this)
                    .load(new File(imagePath))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView); // Tải ảnh chất lượng gốc
        }
    }
}