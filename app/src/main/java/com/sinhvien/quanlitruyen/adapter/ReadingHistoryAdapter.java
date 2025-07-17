package com.sinhvien.quanlitruyen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.ReadingHistory;

import java.util.List;

public class ReadingHistoryAdapter extends RecyclerView.Adapter<ReadingHistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<ReadingHistory> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClicked(int maTruyen);
    }

    public ReadingHistoryAdapter(Context context, List<ReadingHistory> historyList, OnHistoryClickListener listener) {
        this.context = context;
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reading_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ReadingHistory history = historyList.get(position);
        holder.tvHistory.setText("Đã đọc truyện \"" + history.getTenTruyen() + "\" - " + history.getReadTimestamp());
        holder.itemView.setOnClickListener(v -> listener.onHistoryClicked(history.getMaTruyen()));
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistory;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistory = itemView.findViewById(R.id.tvHistory);
        }
    }
}