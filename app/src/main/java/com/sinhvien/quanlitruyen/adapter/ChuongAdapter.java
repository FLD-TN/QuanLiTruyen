package com.sinhvien.quanlitruyen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.Chuong;

import java.util.List;

public class ChuongAdapter extends RecyclerView.Adapter<ChuongAdapter.ChuongViewHolder> {
    private Context context;
    private List<Chuong> chuongList;
    private OnChuongActionListener listener; // Sửa tên listener

    // Sửa lại interface để có nhiều hành động
    public interface OnChuongActionListener {
        void onChuongClick(Chuong chuong);
        void onEditChuongClick(Chuong chuong);
    }

    public ChuongAdapter(Context context, List<Chuong> chuongList, OnChuongActionListener listener) {
        this.context = context;
        this.chuongList = chuongList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChuongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chuong, parent, false);
        return new ChuongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChuongViewHolder holder, int position) {
        Chuong chuong = chuongList.get(position);
        holder.textViewTenChuong.setText(chuong.getTenChuong());

        holder.layoutItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChuongClick(chuong);
            }
        });

        holder.btnEditChuong.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditChuongClick(chuong);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chuongList.size();
    }

    static class ChuongViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTenChuong;
        ImageButton btnEditChuong;
        LinearLayout layoutItem;

        ChuongViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTenChuong = itemView.findViewById(R.id.textViewTenChuong);
            btnEditChuong = itemView.findViewById(R.id.btnEditChuong);
            layoutItem = itemView.findViewById(R.id.layout_item_chuong);
        }
    }
}