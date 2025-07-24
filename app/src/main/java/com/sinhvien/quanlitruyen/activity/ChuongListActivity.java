package com.sinhvien.quanlitruyen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

        // Khởi tạo adapter với listener mới
        adapter = new ChuongAdapter(this, chuongList, new ChuongAdapter.OnChuongActionListener() {
            @Override
            public void onChuongClick(Chuong chuong) {
                Intent intent = new Intent(ChuongListActivity.this, DocChuongActivity.class);
                intent.putExtra("MaChuong", chuong.getMaChuong());
                intent.putExtra("MaTruyen", maTruyen);
                startActivity(intent);
            }

            @Override
            public void onEditChuongClick(Chuong chuong) {
                showEditChuongNameDialog(chuong);
            }
        });
        recyclerChuong.setAdapter(adapter);
    }

    // Phương thức mới để hiển thị dialog
    private void showEditChuongNameDialog(final Chuong chuong) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_chuong_name, null);
        final EditText edtNewName = dialogView.findViewById(R.id.edtNewChuongName);
        edtNewName.setText(chuong.getTenChuong());

        builder.setView(dialogView)
                .setPositiveButton("Lưu", (dialog, id) -> {
                    String newName = edtNewName.getText().toString().trim();
                    if (!newName.isEmpty() && !newName.equals(chuong.getTenChuong())) {
                        dbHelper.updateChuongName(chuong.getMaChuong(), newName);
                        Toast.makeText(ChuongListActivity.this, "Đã đổi tên chương", Toast.LENGTH_SHORT).show();
                        loadChuong(); // Tải lại danh sách để cập nhật giao diện
                    }
                })
                .setNegativeButton("Hủy", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}