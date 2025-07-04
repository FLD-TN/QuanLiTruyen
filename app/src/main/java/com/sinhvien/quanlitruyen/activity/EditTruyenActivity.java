package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.Truyen;

public class EditTruyenActivity extends AppCompatActivity {

    private EditText edtTenTruyen, edtMoTa, edtCoverPath;
    private Button btnLuu;
    private DatabaseHelper dbHelper;
    private int maTruyen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_truyen);

        edtTenTruyen = findViewById(R.id.edtTenTruyen);
        edtMoTa = findViewById(R.id.edtMoTa);
        edtCoverPath = findViewById(R.id.edtCoverPath);
        btnLuu = findViewById(R.id.btnLuu);
        dbHelper = new DatabaseHelper(this);

        maTruyen = getIntent().getIntExtra("maTruyen", -1);
        if (maTruyen != -1) {
            Truyen truyen = dbHelper.getTruyen(maTruyen);
            if (truyen != null) {
                edtTenTruyen.setText(truyen.getTenTruyen());
                edtMoTa.setText(truyen.getMoTa());
                edtCoverPath.setText(truyen.getCoverImagePath());
            }
        }

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ten = edtTenTruyen.getText().toString().trim();
                String moTa = edtMoTa.getText().toString().trim();
                String cover = edtCoverPath.getText().toString().trim();

                if (ten.isEmpty() || moTa.isEmpty()) {
                    Toast.makeText(EditTruyenActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.updateTruyen(maTruyen, ten, moTa, cover);
                if (success) {
                    Toast.makeText(EditTruyenActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditTruyenActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
