package com.sinhvien.quanlitruyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

import com.sinhvien.quanlitruyen.activity.TruyenListActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_CBZ_FILE = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imagePaths = new ArrayList<>();
    private File extractFolder;
    private ProgressDialog progressDialog;
    private String lastUriString;
    private DatabaseHelper dbHelper;
    private String selectedQuality = "Medium"; // Mặc định: Trung bình

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setInitialPrefetchItemCount(5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(20);

        Button btnOpenTruyen = findViewById(R.id.btnOpenTruyen);
        btnOpenTruyen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TruyenListActivity.class);
            startActivity(intent);
        });

        // Nút chọn file CBZ
        Button btnPick = findViewById(R.id.button_pick);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });

        // Khởi tạo Spinner chất lượng ảnh
        Spinner qualitySpinner = findViewById(R.id.spinner_quality);
        qualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] qualityOptions = getResources().getStringArray(R.array.quality_options);
                selectedQuality = qualityOptions[position].equals("Thấp") ? "Low" :
                        qualityOptions[position].equals("Cao") ? "High" : "Medium";
                getSharedPreferences("AppSettings", MODE_PRIVATE).edit()
                        .putString("image_quality", selectedQuality)
                        .apply();
                // Cập nhật lại RecyclerView nếu đã có dữ liệu
                if (imageAdapter != null) {
                    imageAdapter.setQuality(selectedQuality);
                    imageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Tải chất lượng đã lưu
        selectedQuality = getSharedPreferences("AppSettings", MODE_PRIVATE)
                .getString("image_quality", "Medium");
        int spinnerPosition = selectedQuality.equals("Low") ? 0 :
                selectedQuality.equals("High") ? 2 : 1;
        qualitySpinner.setSelection(spinnerPosition);

        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang giải nén...");
        progressDialog.setCancelable(false);

        // Kiểm tra và tải imagePaths từ SQLite
        loadCachedImagePaths();
    }

    private void loadCachedImagePaths() {
        extractFolder = new File(getExternalFilesDir(null), "ExtractedCBZ");
        if (lastUriString != null && !lastUriString.isEmpty()) {
            try {
                String fileHash = calculateFileHash(Uri.parse(lastUriString));
                imagePaths = dbHelper.getImagePaths(fileHash);
                if (!imagePaths.isEmpty() && extractFolder.exists()) {
                    imageAdapter = new ImageAdapter(imagePaths, MainActivity.this, selectedQuality);
                    recyclerView.setAdapter(imageAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Yêu cầu quyền truy cập")
                            .setMessage("Ứng dụng cần quyền truy cập bộ nhớ để đọc file CBZ. Vui lòng cấp quyền để tiếp tục.")
                            .setPositiveButton("Cấp quyền", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSION_CODE);
                                }
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE);
                }
                return;
            }
        }
        pickCBZFile();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickCBZFile();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Quyền bị từ chối")
                            .setMessage("Quyền truy cập bộ nhớ bị từ chối. Vui lòng vào cài đặt ứng dụng để cấp quyền.")
                            .setPositiveButton("Mở cài đặt", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                } else {
                    Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối. Không thể mở file CBZ.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void pickCBZFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Chọn file CBZ"), PICK_CBZ_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CBZ_FILE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                lastUriString = uri.toString();
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                new ExtractCBZTask().execute(uri);
            } else {
                Toast.makeText(this, "Không thể mở file CBZ. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String calculateFileHash(Uri uri) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        InputStream is = getContentResolver().openInputStream(uri);
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

    private class ExtractCBZTask extends AsyncTask<Uri, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(Uri... uris) {
            List<String> imagePaths = new ArrayList<>();
            try {
                String fileHash = calculateFileHash(uris[0]);
                extractFolder = new File(getExternalFilesDir(null), "ExtractedCBZ");

                // Kiểm tra cache trong SQLite
                imagePaths = dbHelper.getImagePaths(fileHash);
                if (!imagePaths.isEmpty() && extractFolder.exists()) {
                    return imagePaths;
                }

                // Tạo thư mục tạm
                if (!extractFolder.exists()) {
                    extractFolder.mkdirs();
                } else {
                    for (File f : extractFolder.listFiles()) {
                        f.delete();
                    }
                }

                // Lưu file tạm
                InputStream inputStream = getContentResolver().openInputStream(uris[0]);
                File tmpZip = new File(getCacheDir(), "temp.cbz");
                FileOutputStream out = new FileOutputStream(tmpZip);
                byte[] buffer = new byte[8192];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
                inputStream.close();

                // Giải nén file CBZ
                ZipFile zipFile = new ZipFile(tmpZip);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        File outFile = new File(extractFolder, entry.getName());
                        InputStream is = zipFile.getInputStream(entry);
                        FileOutputStream fos = new FileOutputStream(outFile);
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        is.close();
                        if (entry.getName().toLowerCase().endsWith(".jpg") || entry.getName().toLowerCase().endsWith(".png")) {
                            imagePaths.add(outFile.getAbsolutePath());
                        }
                    }
                }
                zipFile.close();
                tmpZip.delete();

                // Lưu vào SQLite
                dbHelper.saveCBZFile(fileHash, uris[0].toString(), imagePaths);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi khi giải nén file CBZ. Vui lòng thử lại.", Toast.LENGTH_LONG).show());
            }
            return imagePaths;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            progressDialog.dismiss();
            if (result.isEmpty()) {
                Toast.makeText(MainActivity.this, "Không tìm thấy ảnh trong file CBZ.", Toast.LENGTH_LONG).show();
            } else {
                imagePaths = result;
                imageAdapter = new ImageAdapter(imagePaths, MainActivity.this, selectedQuality);
                recyclerView.setAdapter(imageAdapter);
            }
        }
    }
}