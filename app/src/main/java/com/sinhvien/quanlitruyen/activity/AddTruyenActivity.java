package com.sinhvien.quanlitruyen.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;

public class AddTruyenActivity extends AppCompatActivity {
    private static final int PICK_CBZ_FILE = 1;
    private static final int PICK_COVER_IMAGE = 2;

    private EditText edtTenTruyen, edtMoTa, edtImgPath;
    private Button btnLuu, btnChonCbz, btnPickImg;
    private TextView txtFileName;
    private DatabaseHelper dbHelper;
    private ProgressDialog progressDialog;
    private String fileHash;
    private java.io.File extractFolder;
    private String coverImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_truyen);

        dbHelper = new DatabaseHelper(this);
        edtTenTruyen = findViewById(R.id.edtTenTruyen);
        edtMoTa = findViewById(R.id.edtMoTa);
        edtImgPath = findViewById(R.id.edtImgPath);
        btnLuu = findViewById(R.id.btnLuu);
        btnChonCbz = findViewById(R.id.btnChonCbz);
        btnPickImg = findViewById(R.id.btnPickImg);
        txtFileName = findViewById(R.id.txtFileName);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("\u0110ang gi\u1ea3i n\u00e9n...");
        progressDialog.setCancelable(false);

        btnChonCbz.setOnClickListener(v -> pickCBZFile());
        btnPickImg.setOnClickListener(v -> pickCoverImage());

        btnLuu.setOnClickListener(v -> {
            String ten = edtTenTruyen.getText().toString().trim();
            String moTa = edtMoTa.getText().toString().trim();

            if (ten.isEmpty()) {
                edtTenTruyen.setError("Nhập tên truyện");
                return;
            }
            if (fileHash == null) {
                Toast.makeText(this, "Vui lòng chọn file CBZ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!edtImgPath.getText().toString().trim().isEmpty()) {
                coverImagePath = edtImgPath.getText().toString().trim();
            }

            dbHelper.insertTruyen(ten, moTa, fileHash, coverImagePath); // ✅ fix lỗi

            Toast.makeText(this, "Đã lưu truyện mới!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void pickCBZFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Ch\u1ecdn file CBZ"), PICK_CBZ_FILE);
    }

    private void pickCoverImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Ch\u1ecdn \u1ea3nh b\u00eca"), PICK_COVER_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (requestCode == PICK_CBZ_FILE) {
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    txtFileName.setText(uri.getLastPathSegment());
                    new ExtractCBZTask().execute(uri);
                }
            } else if (requestCode == PICK_COVER_IMAGE) {
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    coverImagePath = uri.toString();
                    edtImgPath.setText(coverImagePath);
                }
            }
        }
    }

    private String calculateFileHash(Uri uri) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        java.io.InputStream is = getContentResolver().openInputStream(uri);
        byte[] buffer = new byte[8192];
        int read;
        while ((read = is.read(buffer)) > 0) {
            md.update(buffer, 0, read);
        }
        is.close();
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private class ExtractCBZTask extends AsyncTask<Uri, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Uri... uris) {
            try {
                fileHash = calculateFileHash(uris[0]);
                extractFolder = new java.io.File(getExternalFilesDir(null), "ExtractedCBZ");
                if (!extractFolder.exists()) {
                    extractFolder.mkdirs();
                } else {
                    for (java.io.File f : extractFolder.listFiles()) {
                        f.delete();
                    }
                }

                java.io.InputStream inputStream = getContentResolver().openInputStream(uris[0]);
                java.io.File tmpZip = new java.io.File(getCacheDir(), "temp.cbz");
                java.io.FileOutputStream out = new java.io.FileOutputStream(tmpZip);
                byte[] buffer = new byte[8192];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
                inputStream.close();

                java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(tmpZip);
                java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zipFile.entries();
                java.util.List<String> paths = new java.util.ArrayList<>();
                while (entries.hasMoreElements()) {
                    java.util.zip.ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        java.io.File outFile = new java.io.File(extractFolder, entry.getName());
                        java.io.InputStream is = zipFile.getInputStream(entry);
                        java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile);
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        is.close();
                        if (entry.getName().toLowerCase().endsWith(".jpg") || entry.getName().toLowerCase().endsWith(".png")) {
                            paths.add(outFile.getAbsolutePath());
                        }
                    }
                }
                zipFile.close();
                tmpZip.delete();

                dbHelper.saveCBZFile(fileHash, uris[0].toString(), paths);
                return !paths.isEmpty();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if (!success) {
                Toast.makeText(AddTruyenActivity.this, "Khong tim thay anh trong file CBZ.", Toast.LENGTH_LONG).show();
                fileHash = null;

            }
        }
    }
}
