package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.ChuongAdapter;
import com.sinhvien.quanlitruyen.model.Chuong;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class ChuongListActivity extends AppCompatActivity {
    private RecyclerView recyclerChuong;
    private ChuongAdapter adapter;
    private DatabaseHelper dbHelper;
    private int maTruyen;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuong_list);

        dbHelper = new DatabaseHelper(this);
        recyclerChuong = findViewById(R.id.recyclerChuong);
        toolbar = findViewById(R.id.toolbar);
        recyclerChuong.setLayoutManager(new LinearLayoutManager(this));

        maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        if (maTruyen == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy MaTruyen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set toolbar title with Truyen name
        Truyen truyen = dbHelper.getTruyen(maTruyen);
        if (truyen != null) {
            toolbar.setTitle(truyen.getTenTruyen());
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadChuong();
    }

    private void loadChuong() {
        List<Chuong> chuongList = dbHelper.getAllChuongByTruyen(maTruyen);
        if (chuongList.isEmpty()) {
            Toast.makeText(this, "Không có chương nào", Toast.LENGTH_SHORT).show();
        }
        adapter = new ChuongAdapter(this, chuongList, chuong -> {
            Intent intent = new Intent(ChuongListActivity.this, DocChuongActivity.class);
            intent.putExtra("MaChuong", chuong.getMaChuong());
            intent.putExtra("MaTruyen", maTruyen);
            startActivity(intent);
        });
        recyclerChuong.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}