package com.sinhvien.quanlitruyen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.activity.TruyenListActivity;
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



        recyclerTruyen = findViewById(R.id.recyclerRecent);
        dbHelper = new DatabaseHelper(this);
        recyclerTruyen.setLayoutManager(new GridLayoutManager(this, 2));

        LinearLayout itemQuanLi = findViewById(R.id.itemQuanLi);
        itemQuanLi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TruyenListActivity.class);
            startActivity(intent);
        });

        loadRecentTruyen();

    }

    private void loadRecentTruyen() {
        truyenList = dbHelper.getRecentTruyen();
        truyenAdapter = new TruyenAdapter(this, truyenList, null, false);
        recyclerTruyen.setAdapter(truyenAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentTruyen();
    }
}
