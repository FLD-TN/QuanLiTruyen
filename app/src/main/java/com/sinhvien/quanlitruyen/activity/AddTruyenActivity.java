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

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.adapter.TheLoaiSelectionAdapter;
import com.sinhvien.quanlitruyen.model.TheLoai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AddTruyenActivity extends AppCompatActivity {
    private EditText edtTenTruyen, edtMoTa, edtManualCoverPath;
    private Button btnSelectCover, btnSelectCBZ, btnSelectTheLoai, btnLuu;
    private TextView tvCbzStatus;
    private DatabaseHelper dbHelper;
    private List<String> selectedTheLoaiList = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private String coverImagePath;

    private static final int REQUEST_COVER_IMAGE = 1;
    private static final int REQUEST_CBZ_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_truyen);

        dbHelper = new DatabaseHelper(this);
        edtTenTruyen = findViewById(R.id.edtTenTruyen);
        edtMoTa = findViewById(R.id.edtMoTa);
        edtManualCoverPath = findViewById(R.id.edtManualCoverPath);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnSelectCBZ = findViewById(R.id.btnSelectCBZ);
        btnSelectTheLoai = findViewById(R.id.btnSelectTheLoai);
        btnLuu = findViewById(R.id.btnLuu);
        tvCbzStatus = findViewById(R.id.tvCbzStatus);

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

        btnLuu.setOnClickListener(v -> saveTruyen());
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
                    Toast.makeText(AddTruyenActivity.this, "Giải nén CBZ thành công", Toast.LENGTH_SHORT).show();
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvCbzStatus.setText("Lỗi giải nén file CBZ");
                    Toast.makeText(AddTruyenActivity.this, "Lỗi giải nén CBZ", Toast.LENGTH_SHORT).show();
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
        TheLoaiSelectionAdapter adapter = new TheLoaiSelectionAdapter(theLoaiList);
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

        if (tenTruyen.isEmpty() || moTa.isEmpty() || coverImagePath == null || imagePaths.isEmpty()) {
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

        Executors.newSingleThreadExecutor().execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("TenTruyen", tenTruyen);
            values.put("MoTa", moTa);
            values.put("CoverImagePath", coverImagePath);
            values.put("FileHash", "hash_placeholder");
            values.put("IsFavorite", 0);
            values.put("SoChuong", 0);
            values.put("MaTheLoai", -1);
            values.put("Note", "");
            long maTruyen = db.insert("Truyen", null, values);

            for (String tenTheLoai : selectedTheLoaiList) {
                long maTheLoai = dbHelper.insertTheLoai(tenTheLoai);
                if (maTheLoai != -1) {
                    dbHelper.insertTruyenTheLoai((int) maTruyen, (int) maTheLoai);
                }
            }

            for (String imagePath : imagePaths) {
                ContentValues imageValues = new ContentValues();
                imageValues.put("MaTruyen", maTruyen);
                imageValues.put("ImagePath", imagePath);
                db.insert("chapter_images", null, imageValues);
            }

            runOnUiThread(() -> {
                Toast.makeText(AddTruyenActivity.this, "Thêm truyện thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddTruyenActivity.this, AddChuongActivity.class);
                intent.putExtra("MaTruyen", (int) maTruyen);
                startActivity(intent);
                finish();
            });
        });
    }
}