package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.activity.AddTruyenActivity;
import com.sinhvien.quanlitruyen.activity.ChuongListActivity;
import com.sinhvien.quanlitruyen.activity.EditTruyenActivity;
import com.sinhvien.quanlitruyen.adapter.TruyenAdapter;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class TruyenListActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerTruyen;
    private TruyenAdapter adapter;
    private Button btnAddTruyen;
    private SearchView searchView;
    private List<Truyen> truyenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truyen_list);

        dbHelper = new DatabaseHelper(this);
        recyclerTruyen = findViewById(R.id.recyclerTruyen);
        btnAddTruyen = findViewById(R.id.btnAddTruyen);
        searchView = findViewById(R.id.searchView);

        recyclerTruyen.setLayoutManager(new LinearLayoutManager(this));
        loadTruyen("");

        // Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadTruyen(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadTruyen(newText);
                return true;
            }
        });

        btnAddTruyen.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTruyenActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTruyen(searchView.getQuery().toString());
    }

    private void loadTruyen(String query) {
        if (query == null || query.trim().isEmpty()) {
            truyenList = dbHelper.getAllTruyen();
        } else {
            truyenList = dbHelper.searchTruyenByNameOrGenre(query);
        }
        if (truyenList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy truyện nào", Toast.LENGTH_SHORT).show();
        }
        adapter = new TruyenAdapter(this, truyenList, new TruyenAdapter.OnTruyenActionListener() {
            @Override
            public void onTruyenDeleted(int maTruyen) {
                int position = -1;
                for (int i = 0; i < truyenList.size(); i++) {
                    if (truyenList.get(i).getMaTruyen() == maTruyen) {
                        position = i;
                        break;
                    }
                }
                if (position != -1) {
                    dbHelper.deleteTruyen(maTruyen);
                    truyenList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, truyenList.size());
                }
            }

            @Override
            public void onTruyenEdited(int maTruyen) {
                Intent intent = new Intent(TruyenListActivity.this, EditTruyenActivity.class);
                intent.putExtra("MaTruyen", maTruyen);
                startActivity(intent);
            }

            @Override
            public void onTruyenImageClicked(int maTruyen) {
                Intent intent = new Intent(TruyenListActivity.this, ChuongListActivity.class);
                intent.putExtra("MaTruyen", maTruyen);
                startActivity(intent);
            }
        }, true);
        recyclerTruyen.setAdapter(adapter);
    }
}
