package com.sinhvien.quanlitruyen.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;

public class AddTruyenActivity extends AppCompatActivity {
    private EditText edtTenTruyen, edtMoTa;
    private Button btnLuu;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_truyen);

        dbHelper = new DatabaseHelper(this);
        edtTenTruyen = findViewById(R.id.edtTenTruyen);
        edtMoTa = findViewById(R.id.edtMoTa);
        btnLuu = findViewById(R.id.btnLuu);

        btnLuu.setOnClickListener(v -> {
            String ten = edtTenTruyen.getText().toString().trim();
            String moTa = edtMoTa.getText().toString().trim();
            if (ten.isEmpty()) {
                edtTenTruyen.setError("Nhập tên truyện");
                return;
            }
            dbHelper.insertTruyen(ten, moTa);
            Toast.makeText(this, "Đã lưu truyện mới!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
