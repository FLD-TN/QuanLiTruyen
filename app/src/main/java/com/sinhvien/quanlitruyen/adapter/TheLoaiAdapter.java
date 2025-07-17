package com.sinhvien.quanlitruyen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.TheLoai;

import java.util.List;

public class TheLoaiAdapter extends RecyclerView.Adapter<TheLoaiAdapter.TheLoaiViewHolder> {
    private List<TheLoai> theLoaiList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TheLoai theLoai);
    }

    public TheLoaiAdapter(List<TheLoai> theLoaiList) {
        this.theLoaiList = theLoaiList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TheLoaiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_the_loai, parent, false);
        return new TheLoaiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheLoaiViewHolder holder, int position) {
        TheLoai theLoai = theLoaiList.get(position);
        holder.textViewTheLoai.setText(theLoai.getTenTheLoai());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(theLoai);
            }
        });
    }

    @Override
    public int getItemCount() {
        return theLoaiList != null ? theLoaiList.size() : 0;
    }

    public void updateList(List<TheLoai> newList) {
        this.theLoaiList = newList;
        notifyDataSetChanged();
    }

    public TheLoai getItemAt(int position) {
        return theLoaiList.get(position);
    }

    static class TheLoaiViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTheLoai;

        TheLoaiViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTheLoai = itemView.findViewById(R.id.textViewTheLoai);
        }
    }
}