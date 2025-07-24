package com.sinhvien.quanlitruyen.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.ImageAdapter;
import com.sinhvien.quanlitruyen.model.Chuong;

import java.util.List;

public class DocChuongActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageAdapter imageAdapter;
    private DatabaseHelper dbHelper;
    private List<String> imagePaths;
    private int maChuong, maTruyen;
    private int lastVisiblePage = 0;
    private TextView tvPageIndicator;
    private ImageButton btnChangeLayout, btnChangeQuality;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "ReadingPrefs";
    private static final String KEY_READING_MODE = "ReadingMode";
    private static final String KEY_IMAGE_QUALITY = "ImageQuality";

    // Constants for settings
    private static final int MODE_HORIZONTAL = 0; // Lật ngang
    private static final int MODE_VERTICAL = 1;   // Cuộn dọc
    private static final int QUALITY_HIGH = 0;
    private static final int QUALITY_MEDIUM = 1;
    private static final int QUALITY_LOW = 2;

    private int currentReadingMode;
    private int currentImageQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_chuong);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load saved preferences
        currentReadingMode = prefs.getInt(KEY_READING_MODE, MODE_HORIZONTAL);
        currentImageQuality = prefs.getInt(KEY_IMAGE_QUALITY, QUALITY_HIGH);

        viewPager = findViewById(R.id.viewPagerImages);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);
        btnChangeLayout = findViewById(R.id.btnChangeLayout);
        btnChangeQuality = findViewById(R.id.btnChangeQuality);

        maChuong = getIntent().getIntExtra("MaChuong", -1);
        maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        if (maChuong == -1 || maTruyen == -1) {
            finish();
            return;
        }

        dbHelper.insertReadingHistory(maTruyen);
        imagePaths = dbHelper.getImagePathsByChuong(maChuong);

        setupViewPager();
        updatePageIndicator();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                lastVisiblePage = position;
                updatePageIndicator();
            }
        });

        int lastPageIndex = dbHelper.getLastReadPage(maTruyen, maChuong);
        if (lastPageIndex >= 0 && lastPageIndex < imagePaths.size()) {
            showContinueReadingPopup(lastPageIndex);
        }

        btnChangeLayout.setOnClickListener(v -> showLayoutDialog());
        btnChangeQuality.setOnClickListener(v -> showQualityDialog());
    }

    private void setupViewPager() {
        imageAdapter = new ImageAdapter(this, imagePaths, currentImageQuality);
        viewPager.setAdapter(imageAdapter);
        if (currentReadingMode == MODE_VERTICAL) {
            viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        } else {
            viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        }
        // Restore to last read page after setting up adapter
        viewPager.setCurrentItem(lastVisiblePage, false);
    }

    private void showLayoutDialog() {
        final String[] modes = {"Lật ngang", "Cuộn dọc"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn chế độ đọc")
                .setSingleChoiceItems(modes, currentReadingMode, (dialog, which) -> {
                    if (currentReadingMode != which) {
                        currentReadingMode = which;
                        prefs.edit().putInt(KEY_READING_MODE, currentReadingMode).apply();
                        Toast.makeText(this, "Đã đổi sang chế độ " + modes[which], Toast.LENGTH_SHORT).show();
                        // Re-setup ViewPager
                        setupViewPager();
                    }
                    dialog.dismiss();
                });
        builder.create().show();
    }

    private void showQualityDialog() {
        final String[] qualities = {"Cao", "Trung bình", "Thấp"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn chất lượng ảnh")
                .setSingleChoiceItems(qualities, currentImageQuality, (dialog, which) -> {
                    if (currentImageQuality != which) {
                        currentImageQuality = which;
                        prefs.edit().putInt(KEY_IMAGE_QUALITY, currentImageQuality).apply();
                        Toast.makeText(this, "Chất lượng ảnh: " + qualities[which], Toast.LENGTH_SHORT).show();
                        // Re-setup ViewPager with new quality
                        setupViewPager();
                    }
                    dialog.dismiss();
                });
        builder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            viewPager.setCurrentItem(lastPageIndex, false); // No smooth scroll
            lastVisiblePage = lastPageIndex;
            updatePageIndicator();
            dialog.dismiss();
        });

        btnReadFromStart.setOnClickListener(v -> {
            viewPager.setCurrentItem(0, false);
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
            tvPageIndicator.setText(currentPage + " / " + totalPages);
        }
    }
}