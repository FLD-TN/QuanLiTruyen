package com.sinhvien.quanlitruyen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.activity.ChuongListActivity;
import com.sinhvien.quanlitruyen.activity.SearchActivity;
import com.sinhvien.quanlitruyen.activity.TruyenListActivity;
import com.sinhvien.quanlitruyen.adapter.TruyenAdapter;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TruyenAdapter.OnTruyenClickListener {

    private EditText edtSearch;
    private ImageView btnUser;
    private LinearLayout itemQuanLi, itemYeuThich, itemLichSu, itemThongKe;
    private RecyclerView recyclerRecent;

    private TruyenAdapter truyenAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo view
        edtSearch = findViewById(R.id.edtSearch);
        btnUser = findViewById(R.id.btnUser);
        itemQuanLi = findViewById(R.id.itemQuanLi);
        itemYeuThich = findViewById(R.id.itemYeuThich);
        itemLichSu = findViewById(R.id.itemLichSu);
        itemThongKe = findViewById(R.id.itemThongKe);
        recyclerRecent = findViewById(R.id.recyclerRecent);

        dbHelper = new DatabaseHelper(this);

        // Cấu hình recycler
        recyclerRecent.setLayoutManager(new GridLayoutManager(this, 2));
        loadRecentTruyen();

        // Sự kiện
        itemQuanLi.setOnClickListener(v ->
                startActivity(new Intent(this, TruyenListActivity.class)));

        itemYeuThich.setOnClickListener(v ->
                Toast.makeText(this, "Mở danh sách yêu thích (chưa cài)", Toast.LENGTH_SHORT).show());

        itemLichSu.setOnClickListener(v ->
                Toast.makeText(this, "Mở lịch sử đọc (chưa cài)", Toast.LENGTH_SHORT).show());

        itemThongKe.setOnClickListener(v ->
                Toast.makeText(this, "Mở thống kê thời gian (chưa cài)", Toast.LENGTH_SHORT).show());

        btnUser.setOnClickListener(v ->
                Toast.makeText(this, "Thông tin tài khoản (chưa cài)", Toast.LENGTH_SHORT).show());

        Button btnSearchEngine = findViewById(R.id.btnSearchEngine);
        btnSearchEngine.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void loadRecentTruyen() {
        List<Truyen> recentList = dbHelper.getRecentTruyen(); // TODO: viết hàm này nếu chưa có
        if (recentList == null || recentList.isEmpty()) {
            // Tạo dữ liệu ảo nếu chưa có
            recentList = List.of(
                    new Truyen(1, "Truyện 1", "Mô tả truyện 1", "", false, 0, R.drawable.anh_bia_manga_gojo),
                    new Truyen(2, "Truyện 2", "Mô tả truyện 2", "", false, 0, R.drawable.anh_bia_manga_mha),
                    new Truyen(3, "Truyện 3", "Mô tả truyện 3", "", false, 0, R.drawable.anh_bia_manga_naruto),
                    new Truyen(4, "Truyện 3", "Mô tả truyện 3", "", false, 0, R.drawable.anh_bia_manga_onepice)

            );
        }
        truyenAdapter = new TruyenAdapter(recentList, this);
        recyclerRecent.setAdapter(truyenAdapter);
    }

    @Override
    public void onTruyenClick(Truyen truyen) {
        Intent intent = new Intent(this, ChuongListActivity.class);
        intent.putExtra("MaTruyen", truyen.getMaTruyen());
        startActivity(intent);
    }
}
