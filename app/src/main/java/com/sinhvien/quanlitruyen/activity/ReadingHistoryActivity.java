package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.ReadingHistoryAdapter;
import com.sinhvien.quanlitruyen.model.ReadingHistory;

import java.util.List;

public class ReadingHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerHistory;
    private ReadingHistoryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_history);

        dbHelper = new DatabaseHelper(this);
        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadHistory();
    }

    private void loadHistory() {
        List<ReadingHistory> historyList = dbHelper.getReadingHistory();
        adapter = new ReadingHistoryAdapter(this, historyList, maTruyen -> {
            Intent intent = new Intent(this, ChuongListActivity.class);
            intent.putExtra("MaTruyen", maTruyen);
            startActivity(intent);
        });
        recyclerHistory.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}