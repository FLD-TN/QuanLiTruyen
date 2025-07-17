package com.sinhvien.quanlitruyen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.activity.ReadingHistoryActivity;
import com.sinhvien.quanlitruyen.activity.StatisticsActivity;
import com.sinhvien.quanlitruyen.activity.TruyenListActivity;
import com.sinhvien.quanlitruyen.activity.TheLoaiManagementActivity;
import com.sinhvien.quanlitruyen.adapter.TruyenAdapter;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerTruyen;
    private TruyenAdapter truyenAdapter;
    private List<Truyen> truyenList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerTruyen = findViewById(R.id.recyclerRecent);
        recyclerTruyen.setLayoutManager(new GridLayoutManager(this, 2));

        LinearLayout itemQuanLi = findViewById(R.id.itemQuanLi);
        itemQuanLi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TruyenListActivity.class);
            startActivity(intent);
        });

        LinearLayout btnManageTheLoai = findViewById(R.id.btnManageTheLoai);
        btnManageTheLoai.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TheLoaiManagementActivity.class);
            startActivity(intent);
        });

        LinearLayout btnReadingHistory = findViewById(R.id.itemLichSu);
        btnReadingHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReadingHistoryActivity.class);
            startActivity(intent);
        });

        LinearLayout btnStatistics = findViewById(R.id.itemThongKe);
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        loadRecentTruyen();
    }

    private void loadRecentTruyen() {
        truyenList = dbHelper.getRecentTruyen();
        if (truyenList.isEmpty()) {
            Toast.makeText(this, "Không có truyện gần đây", Toast.LENGTH_SHORT).show();
        }
        truyenAdapter = new TruyenAdapter(this, truyenList);
        recyclerTruyen.setAdapter(truyenAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentTruyen();
    }
}