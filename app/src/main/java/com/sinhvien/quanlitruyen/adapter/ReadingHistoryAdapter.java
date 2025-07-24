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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        // THAY ĐỔI LỚN: Chuyển đổi và định dạng lại timestamp
        String formattedTime = formatTimestamp(history.getReadTimestamp());

        holder.tvHistory.setText("Đã đọc truyện \"" + history.getTenTruyen() + "\" lúc " + formattedTime);
        holder.itemView.setOnClickListener(v -> listener.onHistoryClicked(history.getMaTruyen()));
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    // HÀM MỚI: Dùng để chuyển đổi múi giờ và định dạng ngày tháng
    private String formatTimestamp(String utcTimestamp) {
        // Định dạng đầu vào từ SQLite (UTC)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Định dạng đầu ra mong muốn (Giờ Việt Nam)
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm 'ngày' dd/MM/yyyy", Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        try {
            Date date = inputFormat.parse(utcTimestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Nếu có lỗi, trả về chuỗi gốc
            return utcTimestamp;
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistory;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistory = itemView.findViewById(R.id.tvHistory);
        }
    }
}