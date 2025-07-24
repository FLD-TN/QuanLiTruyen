package com.sinhvien.quanlitruyen.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.TheLoaiSelectionAdapter;
import com.sinhvien.quanlitruyen.model.TheLoai;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EditTruyenActivity extends AppCompatActivity {
    private EditText edtTenTruyen, edtMoTa, edtManualCoverPath, edtSoChuong, edtTenArc;
    private Button btnSelectCover, btnSelectCBZ, btnSelectTheLoai, btnLuuThongTin, btnTaoLaiChuong;
    private TextView tvCbzStatus;
    private DatabaseHelper dbHelper;
    private List<String> selectedTheLoaiList = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private String coverImagePath;
    private int maTruyen;
    private boolean isCbzChanged = false;

    private static final int REQUEST_COVER_IMAGE = 1;
    private static final int REQUEST_CBZ_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_truyen);

        // Khởi tạo views
        initViews();

        maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        if (maTruyen == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy MaTruyen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải và điền dữ liệu cũ vào các ô
        loadTruyenData();
        // Cài đặt các listener
        setupListeners();
    }

    // HÀM MỚI: Gom khởi tạo view vào một nơi
    private void initViews() {
        dbHelper = new DatabaseHelper(this);
        edtTenTruyen = findViewById(R.id.edtTenTruyen);
        edtMoTa = findViewById(R.id.edtMoTa);
        edtManualCoverPath = findViewById(R.id.edtManualCoverPath);
        edtSoChuong = findViewById(R.id.edtSoChuong);
        edtTenArc = findViewById(R.id.edtTenArc);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnSelectCBZ = findViewById(R.id.btnSelectCBZ);
        btnSelectTheLoai = findViewById(R.id.btnSelectTheLoai);
        btnLuuThongTin = findViewById(R.id.btnLuuThongTin);
        btnTaoLaiChuong = findViewById(R.id.btnTaoLaiChuong);
        tvCbzStatus = findViewById(R.id.tvCbzStatus);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // HÀM MỚI: Gom các listener vào một nơi
    private void setupListeners() {
        btnSelectCover.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_COVER_IMAGE);
        });

        btnSelectCBZ.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_CBZ_FILE);
        });

        btnSelectTheLoai.setOnClickListener(v -> showTheLoaiDialog());
        btnLuuThongTin.setOnClickListener(v -> saveBasicInfo());
        btnTaoLaiChuong.setOnClickListener(v -> showRecreateConfirmationDialog());
    }

    // THAY ĐỔI LỚN: Hàm này giờ đã điền cả số chương
    private void loadTruyenData() {
        Truyen truyen = dbHelper.getTruyen(maTruyen);
        if (truyen != null) {
            edtTenTruyen.setText(truyen.getTenTruyen());
            edtMoTa.setText(truyen.getMoTa());
            edtManualCoverPath.setText(truyen.getCoverImagePath());
            edtSoChuong.setText(String.valueOf(truyen.getSoChuong()));
            coverImagePath = truyen.getCoverImagePath();
            selectedTheLoaiList = dbHelper.getTheLoaiByTruyen(maTruyen);
        }
    }

    // HÀM MỚI: Chỉ lưu thông tin cơ bản, không đụng đến chương
    private void saveBasicInfo() {
        String tenTruyen = edtTenTruyen.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();
        String manualCoverPath = edtManualCoverPath.getText().toString().trim();

        if (tenTruyen.isEmpty()) {
            Toast.makeText(this, "Tên truyện không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate cover image path
        if (manualCoverPath.startsWith("res://")) {
            // logic xử lý res://
        } else {
            coverImagePath = manualCoverPath.isEmpty() ? coverImagePath : manualCoverPath;
            if (coverImagePath != null && !coverImagePath.isEmpty()) {
                File coverFile = new File(coverImagePath);
                if (!coverFile.exists()) {
                    Toast.makeText(this, "Đường dẫn ảnh bìa không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh bìa", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues truyenValues = new ContentValues();
                truyenValues.put("TenTruyen", tenTruyen);
                truyenValues.put("MoTa", moTa);
                truyenValues.put("CoverImagePath", coverImagePath);
                db.update("Truyen", truyenValues, "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});

                db.delete("Truyen_TheLoai", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
                for (String tenTheLoai : selectedTheLoaiList) {
                    long maTheLoai = dbHelper.insertTheLoai(tenTheLoai);
                    if (maTheLoai != -1) {
                        dbHelper.insertTruyenTheLoai(maTruyen, (int) maTheLoai);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            runOnUiThread(() -> {
                Toast.makeText(EditTruyenActivity.this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    // HÀM MỚI: Hiển thị dialog xác nhận trước khi tạo lại chương
    private void showRecreateConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận tạo lại chương")
                .setMessage("Hành động này sẽ XÓA TẤT CẢ chương hiện tại và tên chương bạn đã sửa thủ công. Bạn có chắc chắn muốn tiếp tục?")
                .setPositiveButton("Chắc chắn", (dialog, which) -> recreateChapters())
                .setNegativeButton("Hủy", null)
                .show();
    }

    // HÀM MỚI: Logic tạo lại chương, được tách riêng ra
    private void recreateChapters() {
        String soChuongStr = edtSoChuong.getText().toString().trim();
        String tenArc = edtTenArc.getText().toString().trim();

        if (!isCbzChanged) {
            Toast.makeText(this, "Bạn phải chọn một file CBZ mới để tạo lại chương", Toast.LENGTH_LONG).show();
            return;
        }
        if (soChuongStr.isEmpty()) {
            Toast.makeText(this, "Bạn phải nhập tổng số chương", Toast.LENGTH_SHORT).show();
            return;
        }

        int soChuongMoi;
        try {
            soChuongMoi = Integer.parseInt(soChuongStr);
            if (soChuongMoi <= 0) {
                Toast.makeText(this, "Số chương phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số chương không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                // Cập nhật lại số chương trong bảng Truyen
                ContentValues truyenValues = new ContentValues();
                truyenValues.put("SoChuong", soChuongMoi);
                db.update("Truyen", truyenValues, "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});

                // Xóa chương và ảnh cũ
                db.delete("Chuong", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
                db.delete("chapter_images", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});

                // Tạo lại chương và ảnh mới
                int totalImages = imagePaths.size();
                int imagesPerChapter = totalImages / soChuongMoi;
                int remainderImages = totalImages % soChuongMoi;

                for (int i = 0; i < soChuongMoi; i++) {
                    ContentValues chuongValues = new ContentValues();
                    chuongValues.put("MaTruyen", maTruyen);
                    String tenChuong;
                    if (tenArc.isEmpty()) {
                        tenChuong = "Chương " + (i + 1);
                    } else {
                        tenChuong = tenArc + " - Chương " + (i + 1);
                    }
                    chuongValues.put("TenChuong", tenChuong);
                    chuongValues.put("SoChuong", i + 1);
                    long maChuong = db.insert("Chuong", null, chuongValues);

                    int startIndex = i * imagesPerChapter;
                    int endIndex = startIndex + imagesPerChapter;
                    if (i == soChuongMoi - 1) {
                        endIndex += remainderImages;
                    }
                    for (int j = startIndex; j < endIndex && j < totalImages; j++) {
                        ContentValues imageValues = new ContentValues();
                        imageValues.put("MaTruyen", maTruyen);
                        imageValues.put("MaChuong", maChuong);
                        imageValues.put("ImagePath", imagePaths.get(j));
                        db.insert("chapter_images", null, imageValues);
                    }
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            runOnUiThread(() -> {
                Toast.makeText(EditTruyenActivity.this, "Đã tạo lại các chương thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }


    // ----- CÁC HÀM HELPER KHÁC (GIỮ NGUYÊN) -----

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_COVER_IMAGE) {
                Uri imageUri = data.getData();
                coverImagePath = saveImageToInternalStorage(imageUri);
                if (coverImagePath != null) {
                    edtManualCoverPath.setText(coverImagePath);
                } else {
                    Toast.makeText(this, "Lỗi khi lưu ảnh bìa", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CBZ_FILE) {
                isCbzChanged = true;
                Uri cbzUri = data.getData();
                extractCBZ(cbzUri);
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File outputDir = getFilesDir();
            File outputFile = new File(outputDir, "cover_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void extractCBZ(Uri cbzUri) {
        tvCbzStatus.setVisibility(View.VISIBLE);
        tvCbzStatus.setText("Đang giải nén file CBZ...");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                imagePaths.clear();
                InputStream inputStream = getContentResolver().openInputStream(cbzUri);
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                ZipEntry entry;
                File outputDir = getFilesDir();
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (!entry.isDirectory() && (entry.getName().endsWith(".jpg") || entry.getName().endsWith(".png"))) {
                        File outputFile = new File(outputDir, "cbz_" + System.currentTimeMillis() + "_" + entry.getName());
                        FileOutputStream outputStream = new FileOutputStream(outputFile);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        imagePaths.add(outputFile.getAbsolutePath());
                    }
                    zipInputStream.closeEntry();
                }
                zipInputStream.close();
                inputStream.close();
                runOnUiThread(() -> {
                    tvCbzStatus.setText("Đã giải nén file CBZ thành công");
                    Toast.makeText(EditTruyenActivity.this, "Giải nén CBZ thành công", Toast.LENGTH_SHORT).show();
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvCbzStatus.setText("Lỗi giải nén file CBZ");
                    Toast.makeText(EditTruyenActivity.this, "Lỗi giải nén CBZ", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showTheLoaiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_the_loai_selection, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewTheLoaiSelection);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<TheLoai> theLoaiList = dbHelper.getAllTheLoai();
        if (theLoaiList.isEmpty()) {
            Toast.makeText(this, "Không có thể loại nào, vui lòng thêm thể loại trước", Toast.LENGTH_SHORT).show();
            return;
        }
        TheLoaiSelectionAdapter adapter = new TheLoaiSelectionAdapter(theLoaiList, selectedTheLoaiList);
        recyclerView.setAdapter(adapter);

        builder.setView(dialogView);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            selectedTheLoaiList.clear();
            selectedTheLoaiList.addAll(adapter.getSelectedTenTheLoai());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}