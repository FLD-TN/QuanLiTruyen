package com.sinhvien.quanlitruyen.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;

import java.util.List;
import java.util.concurrent.Executors;

public class AddChuongActivity extends AppCompatActivity {
    private EditText edtTenChuong, edtSoChuong;
    private Button btnLuu;
    private DatabaseHelper dbHelper;
    private int maTruyen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chuong);

        dbHelper = new DatabaseHelper(this);
        edtTenChuong = findViewById(R.id.edtTenChuong);
        edtSoChuong = findViewById(R.id.edtSoChuong);
        btnLuu = findViewById(R.id.btnLuu);

        maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        if (maTruyen == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy MaTruyen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnLuu.setOnClickListener(v -> saveChuong());
    }

    private void saveChuong() {
        String tenChuong = edtTenChuong.getText().toString().trim();
        String soChuongStr = edtSoChuong.getText().toString().trim();

        if (tenChuong.isEmpty() || soChuongStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int soChuong;
        try {
            soChuong = Integer.parseInt(soChuongStr);
            if (soChuong <= 0) {
                Toast.makeText(this, "Số chương phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số chương phải là số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> imagePaths = dbHelper.getImagesByTruyen(maTruyen);
            if (imagePaths.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(AddChuongActivity.this, "Không tìm thấy hình ảnh", Toast.LENGTH_SHORT).show());
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int totalImages = imagePaths.size();
            int imagesPerChapter = totalImages / soChuong;
            int remainderImages = totalImages % soChuong;

            for (int i = 0; i < soChuong; i++) {
                ContentValues chuongValues = new ContentValues();
                chuongValues.put("MaTruyen", maTruyen);
                chuongValues.put("TenChuong", tenChuong + " " + (i + 1));
                chuongValues.put("SoChuong", i + 1);
                long maChuong = db.insert("Chuong", null, chuongValues);

                int startIndex = i * imagesPerChapter;
                int endIndex = startIndex + imagesPerChapter;
                if (i == soChuong - 1) {
                    endIndex += remainderImages; // Assign remainder to last chapter
                }
                for (int j = startIndex; j < endIndex && j < totalImages; j++) {
                    ContentValues imageValues = new ContentValues();
                    imageValues.put("MaTruyen", maTruyen);
                    imageValues.put("MaChuong", maChuong);
                    imageValues.put("ImagePath", imagePaths.get(j));
                    db.insert("chapter_images", null, imageValues);
                }
            }

            // Update SoChuong in Truyen table
            ContentValues truyenValues = new ContentValues();
            truyenValues.put("SoChuong", soChuong);
            db.update("Truyen", truyenValues, "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});

            runOnUiThread(() -> {
                Toast.makeText(AddChuongActivity.this, "Thêm chương thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}