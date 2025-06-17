package com.sinhvien.quanlitruyen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.Chuong;

import java.util.List;

public class ChuongAdapter extends RecyclerView.Adapter<ChuongAdapter.ChuongViewHolder> {
    private List<Chuong> chuongList;
    private OnChuongClickListener listener;

    public interface OnChuongClickListener {
        void onChuongClick(Chuong chuong);
    }

    public ChuongAdapter(List<Chuong> chuongList, OnChuongClickListener listener) {
        this.chuongList = chuongList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChuongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chuong, parent, false);
        return new ChuongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChuongViewHolder holder, int position) {
        Chuong chuong = chuongList.get(position);
        holder.txtTenChuong.setText(chuong.getTenChuong());
        holder.txtSoChuong.setText("Chương: " + chuong.getSoChuong());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChuongClick(chuong);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chuongList.size();
    }

    public static class ChuongViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenChuong, txtSoChuong;
        public ChuongViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenChuong = itemView.findViewById(R.id.txtTenChuong);
            txtSoChuong = itemView.findViewById(R.id.txtSoChuong);
        }
    }
}
