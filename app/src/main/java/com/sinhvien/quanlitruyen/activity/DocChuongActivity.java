package com.sinhvien.quanlitruyen.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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
                        .getString("image_quality", "Low");
                ImageAdapter adapter = new ImageAdapter(paths, this, quality);
                recyclerView.setAdapter(adapter);
            }
        }

//        // Khởi tạo Spinner chất lượng ảnh
//        Spinner qualitySpinner = findViewById(R.id.spinner_quality);
//        qualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String[] qualityOptions = getResources().getStringArray(R.array.quality_options);
//                selectedQuality = qualityOptions[position].equals("Thấp") ? "Low" :
//                        qualityOptions[position].equals("Cao") ? "High" : "Medium";
//                getSharedPreferences("AppSettings", MODE_PRIVATE).edit()
//                        .putString("image_quality", selectedQuality)
//                        .apply();
//                // Cập nhật lại RecyclerView nếu đã có dữ liệu
//                if (imageAdapter != null) {
//                    imageAdapter.setQuality(selectedQuality);
//                    imageAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
//
//        // Tải chất lượng đã lưu
//        selectedQuality = getSharedPreferences("AppSettings", MODE_PRIVATE)
//                .getString("image_quality", "Medium");
//        int spinnerPosition = selectedQuality.equals("Low") ? 0 :
//                selectedQuality.equals("High") ? 2 : 1;
//        qualitySpinner.setSelection(spinnerPosition);
//
//        // Khởi tạo ProgressDialog
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Đang giải nén...");
//        progressDialog.setCancelable(false);
//
//        // Kiểm tra và tải imagePaths từ SQLite
//        loadCachedImagePaths();
//    }
//    }

    }
}


