package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.TruyenAdapter;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class TruyenListActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerTruyen;
    private Button btnAddTruyen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truyen_list);

        dbHelper = new DatabaseHelper(this);
        recyclerTruyen = findViewById(R.id.recyclerTruyen);
        btnAddTruyen = findViewById(R.id.btnAddTruyen);

        recyclerTruyen.setLayoutManager(new LinearLayoutManager(this));
        btnAddTruyen.setOnClickListener(v -> startActivity(new Intent(this, AddTruyenActivity.class)));

        loadTruyen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTruyen();
    }

    private void loadTruyen() {
        List<Truyen> truyenList = dbHelper.getAllTruyen();
        TruyenAdapter adapter = new TruyenAdapter(truyenList, truyen -> {
            Intent intent = new Intent(this, ChuongListActivity.class);
            intent.putExtra("MaTruyen", truyen.getMaTruyen());
            startActivity(intent);
        });
        recyclerTruyen.setAdapter(adapter);
    }
}
