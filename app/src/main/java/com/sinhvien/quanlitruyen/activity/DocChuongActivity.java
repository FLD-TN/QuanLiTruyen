package com.sinhvien.quanlitruyen.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.ImageAdapter;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class DocChuongActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_chuong);

        RecyclerView recyclerView = findViewById(R.id.recyclerDoc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemViewCacheSize(20);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        if (maTruyen != -1) {
            Truyen truyen = dbHelper.getTruyen(maTruyen);
            if (truyen != null) {
                List<String> paths = dbHelper.getImagePaths(truyen.getFileHash());
                String quality = getSharedPreferences("AppSettings", MODE_PRIVATE)
                        .getString("image_quality", "Medium");
                ImageAdapter adapter = new ImageAdapter(paths, this, quality);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
