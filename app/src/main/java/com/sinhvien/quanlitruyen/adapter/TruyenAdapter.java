package com.sinhvien.quanlitruyen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.List;

public class TruyenAdapter extends RecyclerView.Adapter<TruyenAdapter.TruyenViewHolder> {
    private List<Truyen> truyenList;
    private OnTruyenClickListener listener;

    public interface OnTruyenClickListener {
        void onTruyenClick(Truyen truyen);
    }

    public TruyenAdapter(List<Truyen> truyenList, OnTruyenClickListener listener) {
        this.truyenList = truyenList;
        this.listener = listener;
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
        holder.txtTenTruyen.setText(truyen.getTenTruyen());
        holder.txtMoTa.setText(truyen.getMoTa());
        holder.itemView.setOnClickListener(v -> listener.onTruyenClick(truyen));
    }

    @Override
    public int getItemCount() {
        return truyenList.size();
    }

    public static class TruyenViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenTruyen, txtMoTa;
        public TruyenViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenTruyen = itemView.findViewById(R.id.txtTenTruyen);
            txtMoTa = itemView.findViewById(R.id.txtMoTa);
        }
    }
}
