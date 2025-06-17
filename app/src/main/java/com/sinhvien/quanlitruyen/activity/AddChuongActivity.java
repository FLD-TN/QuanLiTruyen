package com.sinhvien.quanlitruyen.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;

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

        btnLuu.setOnClickListener(v -> {
            String tenChuong = edtTenChuong.getText().toString().trim();
            int soChuong = 1;
            try {
                soChuong = Integer.parseInt(edtSoChuong.getText().toString().trim());
            } catch (Exception ignored) {}
            if (tenChuong.isEmpty()) {
                edtTenChuong.setError("Nhập tên chương");
                return;
            }
            dbHelper.insertChuong(maTruyen, tenChuong, soChuong);
            Toast.makeText(this, "Đã thêm chương mới!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
