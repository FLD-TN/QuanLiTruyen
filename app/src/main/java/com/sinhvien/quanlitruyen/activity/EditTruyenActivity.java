package com.sinhvien.quanlitruyen.activity;

import android.content.ContentValues;
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
import com.sinhvien.quanlitruyen.model.Chuong;
import com.sinhvien.quanlitruyen.model.TheLoai;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.concurrent.Executors;

public class EditTruyenActivity extends AppCompatActivity {
    private EditText edtTenTruyen, edtMoTa, edtManualCoverPath, edtSoChuong;
    private Button btnSelectCover, btnSelectCBZ, btnSelectTheLoai, btnLuu, btnDeleteMoTa;
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

        dbHelper = new DatabaseHelper(this);
        edtTenTruyen = findViewById(R.id.edtTenTruyen);
        edtMoTa = findViewById(R.id.edtMoTa);
        edtManualCoverPath = findViewById(R.id.edtManualCoverPath);
        edtSoChuong = findViewById(R.id.edtSoChuong);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnSelectCBZ = findViewById(R.id.btnSelectCBZ);
        btnSelectTheLoai = findViewById(R.id.btnSelectTheLoai);
        btnLuu = findViewById(R.id.btnLuu);
        btnDeleteMoTa = findViewById(R.id.btnDeleteMoTa);
        tvCbzStatus = findViewById(R.id.tvCbzStatus);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        maTruyen = getIntent().getIntExtra("MaTruyen", -1);
        if (maTruyen == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy MaTruyen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTruyenData();

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

        btnDeleteMoTa.setOnClickListener(v -> {
            edtMoTa.setText("");
        });

        btnLuu.setOnClickListener(v -> saveTruyen());
    }

    private void loadTruyenData() {
        Truyen truyen = dbHelper.getTruyen(maTruyen);
        if (truyen != null) {
            edtTenTruyen.setText(truyen.getTenTruyen());
            edtMoTa.setText(truyen.getMoTa());
            edtManualCoverPath.setText(truyen.getCoverImagePath());
            coverImagePath = truyen.getCoverImagePath();
            edtSoChuong.setText(String.valueOf(truyen.getSoChuong()));
            imagePaths = dbHelper.getImagesByTruyen(maTruyen);
            selectedTheLoaiList = dbHelper.getTheLoaiByTruyen(maTruyen);
        }
    }

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

    private void saveTruyen() {
        String tenTruyen = edtTenTruyen.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();
        String manualCoverPath = edtManualCoverPath.getText().toString().trim();
        String soChuongStr = edtSoChuong.getText().toString().trim();

        if (manualCoverPath.startsWith("res://")) {
            String resourceName = manualCoverPath.replace("res://", "");
            int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
            if (resId != 0) {
                coverImagePath = "res://" + resourceName;
            } else {
                Toast.makeText(this, "Tài nguyên drawable không tồn tại: " + resourceName, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            coverImagePath = manualCoverPath.isEmpty() ? coverImagePath : manualCoverPath;
        }

        if (tenTruyen.isEmpty() || coverImagePath == null || (imagePaths.isEmpty() && !isCbzChanged)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin và chọn file CBZ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!coverImagePath.startsWith("res://")) {
            File coverFile = new File(coverImagePath);
            if (!coverFile.exists()) {
                Toast.makeText(this, "Đường dẫn ảnh bìa không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int soChuong;
        try {
            soChuong = soChuongStr.isEmpty() ? 0 : Integer.parseInt(soChuongStr);
            if (soChuong < 0) {
                Toast.makeText(this, "Số chương phải lớn hơn hoặc bằng 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số chương phải là số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Update Truyen
            ContentValues truyenValues = new ContentValues();
            truyenValues.put("TenTruyen", tenTruyen);
            truyenValues.put("MoTa", moTa);
            truyenValues.put("CoverImagePath", coverImagePath);
            truyenValues.put("SoChuong", soChuong);
            db.update("Truyen", truyenValues, "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});

            // Update genres
            db.delete("Truyen_TheLoai", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
            for (String tenTheLoai : selectedTheLoaiList) {
                long maTheLoai = dbHelper.insertTheLoai(tenTheLoai);
                if (maTheLoai != -1) {
                    dbHelper.insertTruyenTheLoai(maTruyen, (int) maTheLoai);
                }
            }

            // Update chapters and images
            if (isCbzChanged || soChuong != dbHelper.getAllChuongByTruyen(maTruyen).size()) {
                db.delete("Chuong", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
                db.delete("chapter_images", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
                if (soChuong > 0) {
                    int totalImages = imagePaths.size();
                    int imagesPerChapter = totalImages / soChuong;
                    int remainderImages = totalImages % soChuong;

                    for (int i = 0; i < soChuong; i++) {
                        ContentValues chuongValues = new ContentValues();
                        chuongValues.put("MaTruyen", maTruyen);
                        chuongValues.put("TenChuong", tenTruyen + " Chương " + (i + 1));
                        chuongValues.put("SoChuong", i + 1);
                        long maChuong = db.insert("Chuong", null, chuongValues);

                        int startIndex = i * imagesPerChapter;
                        int endIndex = startIndex + imagesPerChapter;
                        if (i == soChuong - 1) {
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
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(EditTruyenActivity.this, "Cập nhật truyện thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}