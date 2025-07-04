package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.activity.AddTruyenActivity;
import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.activity.EditTruyenActivity;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.TruyenAdapter;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class TruyenListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TruyenAdapter adapter;
    private DatabaseHelper dbHelper;
    private Button btnAddTruyen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truyen_list);

        recyclerView = findViewById(R.id.recyclerTruyenList);
        btnAddTruyen = findViewById(R.id.btnAddTruyen);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dbHelper = new DatabaseHelper(this);

        btnAddTruyen.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTruyenActivity.class);
            startActivity(intent);
        });

        loadTruyen();
    }

    private void loadTruyen() {
        List<Truyen> truyenList = dbHelper.getAllTruyen();
        adapter = new TruyenAdapter(this, truyenList, new TruyenAdapter.OnTruyenActionListener() {
            @Override
            public void onTruyenDeleted() {
                loadTruyen();
            }

            @Override
            public void onTruyenEdited(int maTruyen) {
                Intent intent = new Intent(TruyenListActivity.this, EditTruyenActivity.class);
                intent.putExtra("maTruyen", maTruyen);
                startActivity(intent);
            }
        }, true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTruyen();
    }
}
