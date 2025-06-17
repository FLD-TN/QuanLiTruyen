package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.ChuongAdapter;
import com.sinhvien.quanlitruyen.model.Chuong;

import java.util.List;

public class ChuongListActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerChuong;
    private Button btnAddChuong;
    private int maTruyen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuong_list);

        dbHelper = new DatabaseHelper(this);
        recyclerChuong = findViewById(R.id.recyclerChuong);
        btnAddChuong = findViewById(R.id.btnAddChuong);

        maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        recyclerChuong.setLayoutManager(new LinearLayoutManager(this));
        btnAddChuong.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddChuongActivity.class);
            intent.putExtra("MaTruyen", maTruyen);
            startActivity(intent);
        });

        loadChuong();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChuong();
    }

    private void loadChuong() {
        List<Chuong> chuongList = dbHelper.getAllChuongByTruyen(maTruyen);
        ChuongAdapter adapter = new ChuongAdapter(chuongList);
        recyclerChuong.setAdapter(adapter);
    }
}
