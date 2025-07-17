package com.sinhvien.quanlitruyen.adapter;

import android.content.Context;
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
    private Context context;
    private List<Chuong> chuongList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Chuong chuong);
    }

    public ChuongAdapter(Context context, List<Chuong> chuongList, OnItemClickListener listener) {
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
        holder.textViewSoChuong.setText(String.format("Ch. %d", chuong.getSoChuong()));
        holder.textViewTenChuong.setText(chuong.getTenChuong());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chuong);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chuongList != null ? chuongList.size() : 0;
    }

    static class ChuongViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSoChuong;
        TextView textViewTenChuong;

        ChuongViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSoChuong = itemView.findViewById(R.id.textViewSoChuong);
            textViewTenChuong = itemView.findViewById(R.id.textViewTenChuong);
        }
    }
}