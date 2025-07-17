package com.sinhvien.quanlitruyen.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.activity.ChuongListActivity;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class TruyenAdapter extends RecyclerView.Adapter<TruyenAdapter.TruyenViewHolder> {
    private Context context;
    private List<Truyen> truyenList;
    private OnTruyenActionListener listener;
    private boolean enableActions;
    private DatabaseHelper dbHelper;

    public TruyenAdapter(Context context, List<Truyen> truyenList, OnTruyenActionListener listener, boolean enableActions) {
        this.context = context;
        this.truyenList = truyenList;
        this.listener = listener;
        this.enableActions = enableActions;
        this.dbHelper = new DatabaseHelper(context);
    }

    public TruyenAdapter(Context context, List<Truyen> truyenList) {
        this(context, truyenList, null, false);
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public TruyenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truyen, parent, false);
        return new TruyenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TruyenViewHolder holder, int position) {
        Truyen truyen = truyenList.get(position);
        holder.textViewTenTruyen.setText(truyen.getTenTruyen());
        holder.textViewMoTa.setText(truyen.getMoTa());

        String coverImagePath = truyen.getCoverImagePath();
        if (coverImagePath != null && coverImagePath.startsWith("res://")) {
            String resourceName = coverImagePath.replace("res://", "");
            int resId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            Glide.with(context)
                    .load(resId)
                    .placeholder(R.drawable.anh_bia_manga_onepice)
                    .error(R.drawable.analytics_icon)
                    .into(holder.imgTruyen);
        } else {
            Glide.with(context)
                    .load(coverImagePath)
                    .placeholder(R.drawable.anh_bia_manga_onepice)
                    .error(R.drawable.analytics_icon)
                    .into(holder.imgTruyen);
        }

        holder.imgTruyen.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTruyenImageClicked(truyen.getMaTruyen());
            } else {
                Intent intent = new Intent(context, ChuongListActivity.class);
                intent.putExtra("MaTruyen", truyen.getMaTruyen());
                context.startActivity(intent);
            }
        });

        holder.btnRate.setOnClickListener(v -> showRatingPopup(truyen.getMaTruyen(), truyen.getNote(), position));

        if (enableActions) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTruyenDeleted(truyen.getMaTruyen());
                }
            });
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTruyenEdited(truyen.getMaTruyen());
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
        }
    }

    private void showRatingPopup(int maTruyen, String currentNote, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.popup_note, null);
        TextInputEditText edtNote = dialogView.findViewById(R.id.edtNote);
        Button btnSaveNote = dialogView.findViewById(R.id.btnSaveNote);

        edtNote.setText(currentNote != null ? currentNote : "");

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnSaveNote.setOnClickListener(v -> {
            String note = edtNote.getText().toString().trim();
            dbHelper.updateTruyenNote(maTruyen, note);
            truyenList.get(position).setNote(note);
            notifyItemChanged(position);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return truyenList != null ? truyenList.size() : 0;
    }

    public interface OnTruyenActionListener {
        void onTruyenDeleted(int maTruyen);
        void onTruyenEdited(int maTruyen);
        void onTruyenImageClicked(int maTruyen);
    }

    static class TruyenViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTruyen;
        TextView textViewTenTruyen;
        TextView textViewMoTa;
        Button btnDelete;
        Button btnEdit;
        Button btnRate;

        TruyenViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTruyen = itemView.findViewById(R.id.imgTruyen);
            textViewTenTruyen = itemView.findViewById(R.id.txtTenTruyen);
            textViewMoTa = itemView.findViewById(R.id.txtMoTa);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}