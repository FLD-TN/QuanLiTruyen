package com.sinhvien.quanlitruyen.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.io.File;
import java.util.List;

public class TruyenAdapter extends RecyclerView.Adapter<TruyenAdapter.TruyenViewHolder> {
    private Context context;
    private List<Truyen> truyenList;
    private OnTruyenActionListener listener;
    private boolean isManageMode;
    private DatabaseHelper dbHelper;

    public interface OnTruyenActionListener {
        void onTruyenDeleted();
        void onTruyenEdited(int maTruyen);
    }

    public TruyenAdapter(Context context, List<Truyen> truyenList, OnTruyenActionListener listener, boolean isManageMode) {
        this.context = context;
        this.truyenList = truyenList;
        this.listener = listener;
        this.isManageMode = isManageMode;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public TruyenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_truyen, parent, false);
        return new TruyenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TruyenViewHolder holder, int position) {
        Truyen truyen = truyenList.get(position);
        holder.txtTenTruyen.setText(truyen.getTenTruyen());
        holder.txtMoTa.setText(truyen.getMoTa());

        String coverPath = truyen.getCoverImagePath();
        if (coverPath != null && !coverPath.isEmpty()) {
            if (coverPath.startsWith("/")) {
                Glide.with(context).load(new File(coverPath)).into(holder.imageView);
            } else {
                int resId = context.getResources().getIdentifier(coverPath, "drawable", context.getPackageName());
                if (resId != 0) {
                    holder.imageView.setImageResource(resId);
                } else {
                    holder.imageView.setImageResource(truyen.getImageResId());
                }
            }
        } else {
            holder.imageView.setImageResource(truyen.getImageResId());
        }

        // Show/Hide quản lý
        if (isManageMode) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        // Xử lý nút sửa
        holder.btnEdit.setOnClickListener(view -> {
            if (listener != null) listener.onTruyenEdited(truyen.getMaTruyen());
        });

        // Xử lý nút xoá
        holder.btnDelete.setOnClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xoá truyện")
                    .setMessage("Bạn có chắc chắn muốn xoá truyện này không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        dbHelper.deleteTruyen(truyen.getMaTruyen());
                        if (listener != null) listener.onTruyenDeleted();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        holder.btnFlag.setOnClickListener(v -> {
            // Inflate popup_note.xml
            View popupView = LayoutInflater.from(context).inflate(R.layout.popup_note, null);
            EditText edtNote = popupView.findViewById(R.id.edtNote);
            Button btnSave = popupView.findViewById(R.id.btnSaveNote);

            // Gán note hiện tại nếu có (lưu bằng SharedPreferences)
            SharedPreferences prefs = context.getSharedPreferences("TruyenNotes", Context.MODE_PRIVATE);
            String key = "note_" + truyen.getMaTruyen();
            String currentNote = prefs.getString(key, "");
            edtNote.setText(currentNote);

            // Tạo dialog
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(popupView)
                    .create();

            btnSave.setOnClickListener(view -> {
                String newNote = edtNote.getText().toString();
                prefs.edit().putString(key, newNote).apply();
                Toast.makeText(context, "Đã lưu ghi chú", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return truyenList.size();
    }

    public static class TruyenViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTenTruyen, txtMoTa;
        ImageView btnEdit, btnDelete, btnFlag;

        public TruyenViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            txtTenTruyen = itemView.findViewById(R.id.txtTenTruyen);
            txtMoTa = itemView.findViewById(R.id.txtMoTa);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnFlag = itemView.findViewById(R.id.btnNote);
        }
    }
}
