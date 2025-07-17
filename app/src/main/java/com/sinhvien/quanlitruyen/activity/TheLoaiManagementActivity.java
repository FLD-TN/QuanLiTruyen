package com.sinhvien.quanlitruyen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.TheLoaiAdapter;
import com.sinhvien.quanlitruyen.model.TheLoai;

import java.util.List;

public class TheLoaiManagementActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerViewTheLoai;
    private TheLoaiAdapter theLoaiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_loai_management);

        dbHelper = new DatabaseHelper(this);
        recyclerViewTheLoai = findViewById(R.id.recyclerViewTheLoai);
        Button btnAddTheLoai = findViewById(R.id.btnAddTheLoai);

        // Set up RecyclerView
        recyclerViewTheLoai.setLayoutManager(new LinearLayoutManager(this));
        loadTheLoai();

        btnAddTheLoai.setOnClickListener(v -> showAddTheLoaiDialog());

        // Set click listener for RecyclerView items
        theLoaiAdapter.setOnItemClickListener(theLoai -> showEditDeleteDialog(theLoai));
    }

    private void loadTheLoai() {
        List<TheLoai> theLoaiList = dbHelper.getAllTheLoai();
        if (theLoaiAdapter == null) {
            theLoaiAdapter = new TheLoaiAdapter(theLoaiList);
            recyclerViewTheLoai.setAdapter(theLoaiAdapter);
        } else {
            theLoaiAdapter.updateList(theLoaiList);
        }
    }

    private void showAddTheLoaiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm thể loại mới");

        final EditText input = new EditText(this);
        input.setHint("Tên thể loại");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newGenre = input.getText().toString().trim();
            if (!newGenre.isEmpty()) {
                long result = dbHelper.insertTheLoai(newGenre);
                if (result != -1) {
                    Toast.makeText(this, "Thể loại thêm thành công", Toast.LENGTH_SHORT).show();
                    loadTheLoai();
                } else {
                    Toast.makeText(this, "Thể loại đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEditDeleteDialog(TheLoai theLoai) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa");

        final EditText input = new EditText(this);
        input.setText(theLoai.getTenTheLoai());
        input.setHint("Tên thể loại");
        builder.setView(input);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(theLoai.getTenTheLoai())) {
                int result = dbHelper.updateTheLoai(theLoai.getMaTheLoai(), newName);
                if (result > 0) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadTheLoai();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton("Xóa", (dialog, which) -> {
            dbHelper.deleteTheLoai(theLoai.getMaTheLoai());
            Toast.makeText(this, "Đã xóa thể loại", Toast.LENGTH_SHORT).show();
            loadTheLoai();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}