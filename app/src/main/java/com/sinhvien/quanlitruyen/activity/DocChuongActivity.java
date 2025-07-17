 package com.sinhvien.quanlitruyen.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.ImageAdapter;
import com.sinhvien.quanlitruyen.model.Chuong;

import java.util.List;

public class DocChuongActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private LinearLayoutManager layoutManager;
    private DatabaseHelper dbHelper;
    private List<String> imagePaths;
    private int maChuong, maTruyen;
    private int lastVisiblePage = 0;
    private TextView tvPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_chuong);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewImages);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        maChuong = getIntent().getIntExtra("MaChuong", -1);
        maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        if (maChuong == -1 || maTruyen == -1) {
            finish();
            return;
        }

        // Log reading event
        dbHelper.insertReadingHistory(maTruyen);

        imagePaths = dbHelper.getImagePathsByChuong(maChuong);
        imageAdapter = new ImageAdapter(this, imagePaths);
        recyclerView.setAdapter(imageAdapter);

        // Initialize page indicator
        updatePageIndicator();

        // Track scroll position
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiblePage = layoutManager.findFirstVisibleItemPosition();
                updatePageIndicator();
            }
        });

        // Check last read page and show popup
        int lastPageIndex = dbHelper.getLastReadPage(maTruyen, maChuong);
        if (lastPageIndex >= 0 && lastPageIndex < imagePaths.size()) {
            showContinueReadingPopup(lastPageIndex);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save last read page
        if (lastVisiblePage >= 0 && lastVisiblePage < imagePaths.size()) {
            dbHelper.saveLastReadPage(maTruyen, maChuong, lastVisiblePage);
        }
    }

    private void showContinueReadingPopup(int lastPageIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.popup_continue_reading, null);
        Button btnContinue = dialogView.findViewById(R.id.btnContinue);
        Button btnReadFromStart = dialogView.findViewById(R.id.btnReadFromStart);

        Chuong chuong = dbHelper.getChuong(maChuong);
        String chapterName = chuong != null ? chuong.getTenChuong() : "Chương " + maChuong;
        builder.setTitle("Tiếp tục đọc?");
        builder.setMessage("Bạn đã đọc đến trang " + (lastPageIndex + 1) + " của \"" + chapterName + "\". Tiếp tục từ trang này?");
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnContinue.setOnClickListener(v -> {
            layoutManager.scrollToPosition(lastPageIndex);
            lastVisiblePage = lastPageIndex;
            updatePageIndicator();
            dialog.dismiss();
        });

        btnReadFromStart.setOnClickListener(v -> {
            layoutManager.scrollToPosition(0);
            lastVisiblePage = 0;
            updatePageIndicator();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updatePageIndicator() {
        int totalPages = imagePaths.size();
        int currentPage = lastVisiblePage + 1;
        if (totalPages == 0) {
            tvPageIndicator.setText("Không có trang");
        } else {
            tvPageIndicator.setText("Trang " + currentPage + "/" + totalPages);
        }
    }
}
